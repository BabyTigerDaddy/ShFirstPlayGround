package com.babytigerdaddy.shfirstplayground.data.repository

import android.content.Context
import com.babytigerdaddy.shfirstplayground.domain.model.Episode
import com.babytigerdaddy.shfirstplayground.domain.model.EpisodeQuestion
import com.babytigerdaddy.shfirstplayground.domain.model.QuestionCategory
import com.babytigerdaddy.shfirstplayground.domain.repository.EpisodeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * assets/episodes/*.json 사전 큐레이션 에피소드 로더.
 *
 * v2 prototype 단계는 bluey_s01e01.json 하나만. 한 에피 통과 후 episodes/ 디렉토리에
 * 추가 큐레이션 에피 추가 — 코드 변경 없이 데이터만 늘림.
 */
@Singleton
class JsonEpisodeRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : EpisodeRepository {

    @Volatile
    private var cache: Map<String, Pair<Episode, List<EpisodeQuestion>>>? = null

    private suspend fun store(): Map<String, Pair<Episode, List<EpisodeQuestion>>> =
        withContext(Dispatchers.IO) { cache ?: load().also { cache = it } }

    private fun load(): Map<String, Pair<Episode, List<EpisodeQuestion>>> {
        val files = context.assets.list(ASSET_DIR).orEmpty()
        return files.filter { it.endsWith(".json") }.associate { file ->
            val raw = context.assets.open("$ASSET_DIR/$file").bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val ep = parseEpisode(root.getJSONObject("episode"))
            val qs = parseQuestions(root.getJSONArray("questions"), ep.id)
                .sortedBy { it.displayOrder }
            ep.id to (ep to qs)
        }
    }

    private fun parseEpisode(obj: JSONObject): Episode {
        val charsArr = obj.getJSONArray("characters")
        val characters = List(charsArr.length()) { charsArr.getString(it) }
        return Episode(
            id = obj.getString("id"),
            seriesTitle = obj.getString("seriesTitle"),
            seasonNumber = obj.getInt("seasonNumber"),
            episodeNumber = obj.getInt("episodeNumber"),
            title = obj.getString("title"),
            parentContext = obj.getString("parentContext"),
            coreTheme = obj.getString("coreTheme"),
            characters = characters,
            durationMin = obj.getInt("durationMin"),
            ageRange = obj.getInt("minAge")..obj.getInt("maxAge"),
            attribution = obj.getString("attribution"),
        )
    }

    private fun parseQuestions(
        arr: org.json.JSONArray,
        episodeId: String,
    ): List<EpisodeQuestion> = List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        EpisodeQuestion(
            id = obj.getString("id"),
            episodeId = episodeId,
            category = QuestionCategory.valueOf(obj.getString("category")),
            text = obj.getString("text"),
            context = obj.getString("context"),
            displayOrder = obj.getInt("displayOrder"),
        )
    }

    override suspend fun listEpisodes(): List<Episode> =
        store().values.map { it.first }.sortedWith(
            compareBy({ it.seriesTitle }, { it.seasonNumber }, { it.episodeNumber }),
        )

    override suspend fun getEpisode(id: String): Episode? = store()[id]?.first

    override suspend fun getQuestions(episodeId: String): List<EpisodeQuestion> =
        store()[episodeId]?.second.orEmpty()

    companion object {
        private const val ASSET_DIR = "episodes"
    }
}
