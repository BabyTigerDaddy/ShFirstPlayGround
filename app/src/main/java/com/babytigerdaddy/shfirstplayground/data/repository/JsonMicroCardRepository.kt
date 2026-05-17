package com.babytigerdaddy.shfirstplayground.data.repository

import android.content.Context
import com.babytigerdaddy.shfirstplayground.domain.model.MicroCard
import com.babytigerdaddy.shfirstplayground.domain.model.MicroCardCategory
import com.babytigerdaddy.shfirstplayground.domain.repository.MicroCardRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

/**
 * assets/microcards.json 마이크로카드 라이브러리 로더.
 * 일별 카드는 date.toEpochDay 모듈로 deterministic 선택 — 같은 날짜 = 같은 카드.
 */
@Singleton
class JsonMicroCardRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : MicroCardRepository {

    @Volatile
    private var cache: List<MicroCard>? = null

    private suspend fun load(): List<MicroCard> = withContext(Dispatchers.IO) {
        cache ?: parse().also { cache = it }
    }

    private fun parse(): List<MicroCard> {
        val raw = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val arr = root.getJSONArray("cards")
        return List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            MicroCard(
                id = obj.getString("id"),
                title = obj.getString("title"),
                body = obj.getString("body"),
                category = MicroCardCategory.valueOf(obj.getString("category")),
            )
        }.sortedBy { it.id }
    }

    override suspend fun listCards(): List<MicroCard> = load()

    override suspend fun cardForDate(date: LocalDate): MicroCard? {
        val all = load()
        if (all.isEmpty()) return null
        val index = (date.toEpochDay().absoluteValue % all.size).toInt()
        return all[index]
    }

    companion object {
        private const val ASSET_PATH = "microcards.json"
    }
}
