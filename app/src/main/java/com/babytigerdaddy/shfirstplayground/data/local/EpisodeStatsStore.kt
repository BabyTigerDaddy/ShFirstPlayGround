package com.babytigerdaddy.shfirstplayground.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 에피소드 시청·질문 사용·도움 평점 + 앱 마지막 오픈 timestamp 영속.
 *
 * v2 prototype success criteria 측정용:
 *  (a) `questionsUsed.size >= 1` → 1개 이상 질문 던짐
 *  (b) `usefulnessRating >= 1` → 부모 self-report 도움 점수
 *  (c) `(lastOpenedAt − episodeViewedAt) > 24h` → 다음날 재오픈
 *
 * DataStore Preferences 사용 (Room over-engineering 회피). 한 에피 기준 schema 단순.
 */
val Context.episodeStatsDataStore: DataStore<Preferences> by preferencesDataStore(name = "episode_stats")

@Singleton
class EpisodeStatsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val store get() = context.episodeStatsDataStore

    suspend fun markEpisodeViewed(episodeId: String, now: Long = System.currentTimeMillis()) {
        store.edit { prefs ->
            prefs[viewedAtKey(episodeId)] = now
        }
    }

    suspend fun markQuestionUsed(episodeId: String, questionId: String) {
        store.edit { prefs ->
            val key = questionsUsedKey(episodeId)
            val current = prefs[key].orEmpty().split(SEP).filter { it.isNotEmpty() }.toMutableSet()
            current.add(questionId)
            prefs[key] = current.joinToString(SEP)
        }
    }

    suspend fun setUsefulnessRating(episodeId: String, rating: Int) {
        store.edit { prefs ->
            prefs[ratingKey(episodeId)] = rating.coerceIn(1, 5)
        }
    }

    suspend fun markAppOpened(now: Long = System.currentTimeMillis()) {
        store.edit { prefs ->
            prefs[LAST_OPENED_AT] = now
        }
    }

    fun observeStats(episodeId: String): Flow<EpisodeStats> =
        store.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { prefs ->
                EpisodeStats(
                    episodeViewedAt = prefs[viewedAtKey(episodeId)],
                    questionsUsed = prefs[questionsUsedKey(episodeId)]
                        ?.split(SEP)
                        ?.filter { it.isNotEmpty() }
                        ?.toSet()
                        .orEmpty(),
                    usefulnessRating = prefs[ratingKey(episodeId)],
                    lastOpenedAt = prefs[LAST_OPENED_AT],
                )
            }

    private fun viewedAtKey(episodeId: String) = longPreferencesKey("viewedAt:$episodeId")
    private fun questionsUsedKey(episodeId: String) = stringPreferencesKey("questionsUsed:$episodeId")
    private fun ratingKey(episodeId: String) = intPreferencesKey("usefulnessRating:$episodeId")

    companion object {
        private const val SEP = ","
        private val LAST_OPENED_AT = longPreferencesKey("lastOpenedAt")
    }
}

data class EpisodeStats(
    val episodeViewedAt: Long? = null,
    val questionsUsed: Set<String> = emptySet(),
    val usefulnessRating: Int? = null,
    val lastOpenedAt: Long? = null,
)
