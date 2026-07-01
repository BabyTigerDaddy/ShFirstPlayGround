package com.babytigerdaddy.shfirstplayground.data.remote

import com.babytigerdaddy.shfirstplayground.data.local.BackupPreferenceStore
import com.babytigerdaddy.shfirstplayground.data.local.database.AccountDao
import com.babytigerdaddy.shfirstplayground.data.local.database.AccountEntity
import com.babytigerdaddy.shfirstplayground.data.local.database.HoldingDao
import com.babytigerdaddy.shfirstplayground.data.local.database.HoldingEntity
import com.babytigerdaddy.shfirstplayground.data.local.database.SoldRecordDao
import com.babytigerdaddy.shfirstplayground.data.local.database.SoldRecordEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 클라우드 백업 — 로그인한 계정(uid)의 Firestore 문서에 로컬 기록 전체를 저장·복원.
 *
 * 정책: 로컬 우선(로컬이 원본). 백업 켜면 로컬→클라우드 업로드.
 * 새 폰(로컬 빔)에서 로그인하면 자동 복원, 양쪽 다 있으면 사용자가 명시적으로 복원할 때만 덮어씀.
 * 손익 기록이 실수로 날아가지 않게 restore는 반드시 명시적 호출로만.
 */
@Singleton
class BackupRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val accountDao: AccountDao,
    private val holdingDao: HoldingDao,
    private val soldRecordDao: SoldRecordDao,
    private val store: BackupPreferenceStore,
) {
    var enabled: Boolean
        get() = store.enabled
        set(value) { store.enabled = value }

    val lastBackupAt: Long get() = store.lastBackupAt

    private fun userDoc() =
        auth.currentUser?.uid?.let { firestore.collection("users").document(it) }

    /** 로컬 전체 → 클라우드. 로그인 안 됐으면 실패. */
    suspend fun backup(): Result<Unit> = runCatching {
        val doc = userDoc() ?: error("로그인이 필요해요")
        val data = mapOf(
            "accounts" to accountDao.getAll().map { it.toMap() },
            "holdings" to holdingDao.getAll().map { it.toMap() },
            "soldRecords" to soldRecordDao.getAll().map { it.toMap() },
            "updatedAt" to System.currentTimeMillis(),
        )
        doc.set(data).await()
        store.lastBackupAt = System.currentTimeMillis()
    }

    /** 클라우드 → 로컬 (기존 로컬 전부 지우고 덮어씀). 명시적 호출 전용. */
    suspend fun restore(): Result<Unit> = runCatching {
        val doc = userDoc() ?: error("로그인이 필요해요")
        val snap = doc.get().await()
        if (!snap.exists()) error("클라우드에 백업이 없어요")

        @Suppress("UNCHECKED_CAST")
        val accounts = (snap.get("accounts") as? List<Map<String, Any?>>).orEmpty()
        @Suppress("UNCHECKED_CAST")
        val holdings = (snap.get("holdings") as? List<Map<String, Any?>>).orEmpty()
        @Suppress("UNCHECKED_CAST")
        val solds = (snap.get("soldRecords") as? List<Map<String, Any?>>).orEmpty()

        accountDao.clearAll()
        holdingDao.clearAll()
        soldRecordDao.clearAll()
        accounts.forEach { accountDao.upsert(it.toAccount()) }
        holdings.forEach { holdingDao.upsert(it.toHolding()) }
        solds.forEach { soldRecordDao.upsert(it.toSold()) }
    }

    /** 클라우드에 백업 문서가 있는지. 로그인 안 됐으면 false. */
    suspend fun hasCloudBackup(): Boolean = runCatching {
        val doc = userDoc() ?: return false
        doc.get().await().exists()
    }.getOrDefault(false)

    /** 로컬이 비어 있는지 — 새 폰 자동 복원 판단용. */
    suspend fun isLocalEmpty(): Boolean =
        accountDao.getAll().isEmpty() && holdingDao.getAll().isEmpty() && soldRecordDao.getAll().isEmpty()

    /**
     * 백업 켜기 — 새 폰(로컬 빔)이고 클라우드에 기록 있으면 복원, 아니면 로컬을 클라우드로 올림.
     * 양쪽 다 있는 경우(로컬 있음+클라우드 있음)는 로컬 우선으로 백업(덮어쓰기는 별도 restore 버튼).
     */
    suspend fun enableAndSync(): Result<Unit> = runCatching {
        store.enabled = true
        if (isLocalEmpty() && hasCloudBackup()) restore().getOrThrow() else backup().getOrThrow()
    }

    /** 백업 끄기 — 클라우드 데이터는 두고 이 폰에서만 자동 백업 중단. */
    fun disable() { store.enabled = false }
}

// ---- Entity <-> Firestore Map (LocalDate/LocalDateTime은 문자열로, 숫자는 Number로 복원) ----

private fun AccountEntity.toMap() = mapOf(
    "id" to id, "name" to name, "sortOrder" to sortOrder, "createdAt" to createdAt.toString(),
)

private fun Map<String, Any?>.toAccount() = AccountEntity(
    id = this["id"] as String,
    name = this["name"] as String,
    sortOrder = (this["sortOrder"] as Number).toInt(),
    createdAt = LocalDateTime.parse(this["createdAt"] as String),
)

private fun HoldingEntity.toMap() = mapOf(
    "id" to id, "accountId" to accountId, "code" to code, "ticker" to ticker,
    "buyPrice" to buyPrice, "currentPrice" to currentPrice, "quantity" to quantity,
    "entryDate" to entryDate.toString(), "createdAt" to createdAt.toString(),
)

private fun Map<String, Any?>.toHolding() = HoldingEntity(
    id = this["id"] as String,
    accountId = this["accountId"] as String,
    code = this["code"] as String,
    ticker = this["ticker"] as String,
    buyPrice = (this["buyPrice"] as Number).toLong(),
    currentPrice = (this["currentPrice"] as Number).toLong(),
    quantity = (this["quantity"] as Number).toInt(),
    entryDate = LocalDate.parse(this["entryDate"] as String),
    createdAt = LocalDateTime.parse(this["createdAt"] as String),
)

private fun SoldRecordEntity.toMap() = mapOf(
    "id" to id, "accountId" to accountId, "ticker" to ticker,
    "buyPrice" to buyPrice, "sellPrice" to sellPrice, "quantity" to quantity,
    "entryDate" to entryDate.toString(), "soldDate" to soldDate.toString(), "createdAt" to createdAt.toString(),
)

private fun Map<String, Any?>.toSold() = SoldRecordEntity(
    id = this["id"] as String,
    accountId = this["accountId"] as String,
    ticker = this["ticker"] as String,
    buyPrice = (this["buyPrice"] as Number).toLong(),
    sellPrice = (this["sellPrice"] as Number).toLong(),
    quantity = (this["quantity"] as Number).toInt(),
    entryDate = LocalDate.parse(this["entryDate"] as String),
    soldDate = LocalDate.parse(this["soldDate"] as String),
    createdAt = LocalDateTime.parse(this["createdAt"] as String),
)
