package com.babytigerdaddy.shfirstplayground.data.repository

import android.content.Context
import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Genre
import com.babytigerdaddy.shfirstplayground.domain.model.Language
import com.babytigerdaddy.shfirstplayground.domain.model.Theme
import com.babytigerdaddy.shfirstplayground.domain.repository.ContentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * assets/content_library.json에서 콘텐츠 라이브러리를 로드하는 ContentRepository 구현.
 *
 * 초기 라이브러리는 15개 5살 인기 콘텐츠 시드(한국 IP + 글로벌). 추후 Room DB로 마이그레이션
 * 예정이지만 MVP는 정적 JSON으로 시작 — 사용자 데이터 없이도 즉시 작동.
 *
 * 검색은 단순 lowercase contains 매칭(title + aliases). fuzzy 알고리즘 보강은 Phase 2.
 */
@Singleton
class JsonContentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ContentRepository {

    @Volatile
    private var cache: List<Content>? = null

    private suspend fun library(): List<Content> = withContext(Dispatchers.IO) {
        cache ?: loadFromAssets().also { cache = it }
    }

    private fun loadFromAssets(): List<Content> {
        val raw = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val arr = root.getJSONArray("contents")
        return List(arr.length()) { i -> parseContent(arr.getJSONObject(i)) }
    }

    private fun parseContent(obj: JSONObject): Content {
        val themesArr = obj.getJSONArray("themes")
        val themes = List(themesArr.length()) { Theme.valueOf(themesArr.getString(it)) }
        val aliases = obj.optJSONArray("aliases")?.let { a ->
            List(a.length()) { a.getString(it) }
        } ?: emptyList()
        val characters = obj.optJSONArray("characters")?.let { a ->
            List(a.length()) { a.getString(it) }
        } ?: emptyList()
        return Content(
            id = obj.getString("id"),
            title = obj.getString("title"),
            aliases = aliases,
            genre = Genre.valueOf(obj.getString("genre")),
            themes = themes,
            ageRange = obj.getInt("minAge")..obj.getInt("maxAge"),
            language = Language.valueOf(obj.getString("language")),
            characters = characters,
            summary = obj.optString("summary", ""),
        )
    }

    override suspend fun findContent(query: String): List<Content> {
        val all = library()
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all
        return all.filter { content ->
            content.title.lowercase().contains(q) ||
                content.aliases.any { it.lowercase().contains(q) }
        }
    }

    override suspend fun getContent(id: String): Content? =
        library().find { it.id == id }

    companion object {
        private const val ASSET_PATH = "content_library.json"
    }
}
