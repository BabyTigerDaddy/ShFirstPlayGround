package com.babytigerdaddy.shfirstplayground.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.babytigerdaddy.shfirstplayground.domain.model.Memo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * v3 phase 1 success criteria 측정 + onboarding 상태 영속.
 *
 *  - routine 체크: 날짜별 checkedStepIds (csv)
 *  - 마이크로카드 viewed: 날짜별 viewedCardIds (csv)
 *  - onboarding 완료 flag (전역)
 *  - 메모 highlight·challenge (날짜별)
 *
 * 단일 DataStore Preferences 파일(app_state)에 모든 key 통합 — simplicity 우선.
 * Phase 2에서 milestone 누적 view 들어가면 Room으로 마이그레이션 검토.
 */
val Context.appStateDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_state")

@Singleton
class AppStateStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val store get() = context.appStateDataStore

    // ---- onboarding ----

    val onboardingCompleted: Flow<Boolean> = store.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun markOnboardingCompleted() {
        store.edit { it[ONBOARDING_COMPLETED] = true }
    }

    // ---- routine 체크 ----

    fun observeCheckedStepIds(date: LocalDate): Flow<Set<String>> = store.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { it[checkedStepIdsKey(date)].toIdSet() }

    suspend fun toggleStepChecked(date: LocalDate, stepId: String) {
        store.edit { prefs ->
            val key = checkedStepIdsKey(date)
            val current = prefs[key].toIdSet().toMutableSet()
            if (stepId in current) current.remove(stepId) else current.add(stepId)
            prefs[key] = current.joinToString(SEP)
        }
    }

    // ---- 마이크로카드 viewed ----

    suspend fun markCardViewed(date: LocalDate, cardId: String) {
        store.edit { prefs ->
            val key = viewedCardIdsKey(date)
            val current = prefs[key].toIdSet().toMutableSet()
            current.add(cardId)
            prefs[key] = current.joinToString(SEP)
        }
    }

    // ---- memo ----

    suspend fun getMemo(date: LocalDate): Memo? {
        val prefs = store.data.first()
        val h = prefs[memoHighlightKey(date)]
        val c = prefs[memoChallengeKey(date)]
        return if (h.isNullOrBlank() && c.isNullOrBlank()) {
            null
        } else {
            Memo(
                id = "memo-$date",
                date = date,
                highlight = h?.takeIf { it.isNotBlank() },
                challenge = c?.takeIf { it.isNotBlank() },
            )
        }
    }

    suspend fun saveMemo(memo: Memo) {
        store.edit { prefs ->
            prefs[memoHighlightKey(memo.date)] = memo.highlight.orEmpty()
            prefs[memoChallengeKey(memo.date)] = memo.challenge.orEmpty()
        }
    }

    suspend fun countMemosBetween(start: LocalDate, endInclusive: LocalDate): Int {
        val prefs = store.data.first()
        var count = 0
        var d = start
        while (!d.isAfter(endInclusive)) {
            val h = prefs[memoHighlightKey(d)]
            val c = prefs[memoChallengeKey(d)]
            if (!h.isNullOrBlank() || !c.isNullOrBlank()) count += 1
            d = d.plusDays(1)
        }
        return count
    }

    private fun String?.toIdSet(): Set<String> =
        this?.split(SEP)?.filter { it.isNotEmpty() }?.toSet().orEmpty()

    private fun checkedStepIdsKey(date: LocalDate) =
        stringPreferencesKey("checkedStepIds:$date")

    private fun viewedCardIdsKey(date: LocalDate) =
        stringPreferencesKey("viewedCardIds:$date")

    private fun memoHighlightKey(date: LocalDate) =
        stringPreferencesKey("memo:$date:highlight")

    private fun memoChallengeKey(date: LocalDate) =
        stringPreferencesKey("memo:$date:challenge")

    companion object {
        private const val SEP = ","
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboardingCompleted")
    }
}
