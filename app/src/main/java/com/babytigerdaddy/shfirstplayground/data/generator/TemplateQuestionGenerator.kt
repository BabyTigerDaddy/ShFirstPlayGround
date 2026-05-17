package com.babytigerdaddy.shfirstplayground.data.generator

import android.content.Context
import com.babytigerdaddy.shfirstplayground.domain.generator.QuestionGenerator
import com.babytigerdaddy.shfirstplayground.domain.model.Content
import com.babytigerdaddy.shfirstplayground.domain.model.Question
import com.babytigerdaddy.shfirstplayground.domain.model.QuestionCategory
import com.babytigerdaddy.shfirstplayground.domain.model.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * assets/question_templates.json 기반 메타인지 질문 generator.
 *
 * 알고리즘:
 *   1) Content.themes × template.themeAffinity 가중치로 카테고리(5종) score 계산
 *   2) score 분포로 distinct 카테고리 N개 sample (중복 없이)
 *   3) 각 카테고리에서 themeAffinity 매칭이 좋은 template 후보 중 무작위 선택
 *   4) {title} / {character} placeholder fill
 *
 * 5살 발달 단계 baseline은 EMOTION/EVERYDAY 살짝 높게, COMPARISON 살짝 낮게 (인지 부담).
 */
@Singleton
class TemplateQuestionGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
) : QuestionGenerator {

    @Volatile
    private var cache: List<Template>? = null

    override suspend fun generate(content: Content, count: Int): List<Question> = withContext(Dispatchers.IO) {
        val templates = templates()
        val categories = pickCategories(content, count, templates)
        categories.mapIndexed { index, category ->
            val pool = templates.filter { it.category == category }
            val pick = chooseTemplate(pool, content)
            Question(
                id = "${content.id}-q$index",
                text = fill(pick.pattern, content),
                category = category,
            )
        }
    }

    private fun templates(): List<Template> = cache ?: loadFromAssets().also { cache = it }

    private fun loadFromAssets(): List<Template> {
        val raw = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val arr = root.getJSONArray("templates")
        return List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            val affArr = obj.optJSONArray("themeAffinity")
            val affinity = if (affArr != null) {
                List(affArr.length()) { Theme.valueOf(affArr.getString(it)) }.toSet()
            } else emptySet()
            Template(
                id = obj.getString("id"),
                category = QuestionCategory.valueOf(obj.getString("category")),
                pattern = obj.getString("pattern"),
                affinity = affinity,
            )
        }
    }

    private fun pickCategories(
        content: Content,
        count: Int,
        templates: List<Template>,
    ): List<QuestionCategory> {
        val scores = QuestionCategory.entries.associateWith { cat ->
            val baseline = BASELINE[cat] ?: 1.0
            val pool = templates.filter { it.category == cat }
            val themeScore = pool.maxOfOrNull { tpl ->
                tpl.affinity.intersect(content.themes.toSet()).size.toDouble()
            } ?: 0.0
            baseline + themeScore
        }
        val sorted = scores.entries.sortedByDescending { it.value }
        return sorted.take(count).map { it.key }
    }

    private fun chooseTemplate(pool: List<Template>, content: Content): Template {
        if (pool.isEmpty()) error("No template for required category")
        val ranked = pool.sortedByDescending { tpl ->
            tpl.affinity.intersect(content.themes.toSet()).size
        }
        val topAffinity = ranked.first().affinity.intersect(content.themes.toSet()).size
        val topTier = ranked.filter {
            it.affinity.intersect(content.themes.toSet()).size == topAffinity
        }
        return topTier[Random.nextInt(topTier.size)]
    }

    private fun fill(pattern: String, content: Content): String {
        var text = pattern.replace("{title}", content.title)
        if (text.contains("{character}")) {
            val char = content.characters.firstOrNull() ?: content.title
            text = text.replace("{character}", char)
        }
        return text
    }

    private data class Template(
        val id: String,
        val category: QuestionCategory,
        val pattern: String,
        val affinity: Set<Theme>,
    )

    companion object {
        private const val ASSET_PATH = "question_templates.json"

        // 5살 발달 baseline 가중치. EMOTION·EVERYDAY 우선, COMPARISON은 인지부담 커서 낮게.
        private val BASELINE = mapOf(
            QuestionCategory.EMOTION to 1.5,
            QuestionCategory.RECALL to 1.2,
            QuestionCategory.APPLICATION to 1.2,
            QuestionCategory.COGNITION to 1.0,
            QuestionCategory.COMPARISON to 0.7,
        )
    }
}
