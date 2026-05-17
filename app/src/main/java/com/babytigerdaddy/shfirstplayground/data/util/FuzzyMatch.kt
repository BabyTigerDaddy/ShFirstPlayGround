package com.babytigerdaddy.shfirstplayground.data.util

import kotlin.math.max
import kotlin.math.min

/**
 * Levenshtein 거리 기반 fuzzy 매칭 유틸. 한국어·영어 동일 처리.
 *
 * - distance: 두 문자열 편집 거리
 * - ratio: 0.0~1.0 (1.0 = 완전 일치)
 * - bestRatio: query를 candidates 각각 + alias와 매칭해 최고 점수 반환
 */
internal object FuzzyMatch {

    fun distance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val cost = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            var prev = cost[0]
            cost[0] = i
            for (j in 1..b.length) {
                val current = cost[j]
                cost[j] = if (a[i - 1] == b[j - 1]) {
                    prev
                } else {
                    min(min(cost[j] + 1, cost[j - 1] + 1), prev + 1)
                }
                prev = current
            }
        }
        return cost[b.length]
    }

    fun ratio(a: String, b: String): Double {
        if (a.isEmpty() && b.isEmpty()) return 1.0
        val longer = max(a.length, b.length)
        return 1.0 - distance(a, b).toDouble() / longer
    }

    /**
     * partial match — 짧은 query가 긴 target의 부분과 얼마나 잘 매칭되는지.
     * query가 target의 substring이면 1.0, 그 외엔 sliding window로 최고 ratio.
     */
    fun partialRatio(query: String, target: String): Double {
        val q = query.lowercase()
        val t = target.lowercase()
        if (q.isEmpty()) return 0.0
        if (t.contains(q)) return 1.0
        if (q.length >= t.length) return ratio(q, t)
        var best = 0.0
        for (i in 0..t.length - q.length) {
            val window = t.substring(i, i + q.length)
            val r = ratio(q, window)
            if (r > best) best = r
            if (best == 1.0) break
        }
        return best
    }

    /** query × (title + aliases) 중 최고 점수. */
    fun bestRatio(query: String, title: String, aliases: List<String>): Double {
        if (query.isBlank()) return 0.0
        val candidates = (listOf(title) + aliases).map { it.lowercase() }
        val q = query.lowercase()
        return candidates.maxOf { partialRatio(q, it) }
    }
}
