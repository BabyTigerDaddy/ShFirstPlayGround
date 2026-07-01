package com.babytigerdaddy.shfirstplayground.data.remote

import com.babytigerdaddy.shfirstplayground.domain.repository.StockPriceSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

/**
 * 야후파이낸스 무료 시세 — 앱키·로그인 없이 종목코드로 현재가 조회.
 *
 * 한국 종목 심볼: 코스피 `{code}.KS`, 코스닥 `{code}.KQ`.
 * ★시장에 맞는 심볼을 정확히 써야 한다 — 같은 6자리 코드가 .KS/.KQ에서 서로 다른 종목이라
 * 시장 구분 없이 .KS부터 찍으면 코스닥 종목이 엉뚱한 값을 받는다.
 * v8 chart 엔드포인트(무인증)의 meta.regularMarketPrice를 쓴다. 15~20분 지연.
 */
@Singleton
class YahooStockPriceSource @Inject constructor() : StockPriceSource {

    override suspend fun fetchPrice(code: String, market: String): Long? = withContext(Dispatchers.IO) {
        val clean = code.filter { it.isDigit() }
        if (clean.length < 6) return@withContext null
        val suffix = if (market.uppercase().contains("KOSDAQ")) ".KQ" else ".KS"
        tryFetch(clean + suffix)
    }

    private fun tryFetch(symbol: String): Long? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL("$BASE$symbol?interval=1d&range=1d")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                // 야후가 기본 클라이언트를 막는 경우가 있어 UA 지정.
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android) HoldingNote/1.0")
                connectTimeout = 7000
                readTimeout = 7000
            }
            if (conn.responseCode != 200) return null
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            parsePrice(body)
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    /** chart.result[0].meta.regularMarketPrice 추출. */
    private fun parsePrice(json: String): Long? {
        val result = JSONObject(json)
            .optJSONObject("chart")
            ?.optJSONArray("result")
            ?.optJSONObject(0)
            ?.optJSONObject("meta")
            ?: return null
        if (!result.has("regularMarketPrice")) return null
        val price = result.optDouble("regularMarketPrice", Double.NaN)
        return if (price.isNaN() || price <= 0.0) null else price.roundToLong()
    }

    private companion object {
        const val BASE = "https://query1.finance.yahoo.com/v8/finance/chart/"
    }
}
