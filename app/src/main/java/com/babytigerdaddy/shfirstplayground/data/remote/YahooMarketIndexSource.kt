package com.babytigerdaddy.shfirstplayground.data.remote

import com.babytigerdaddy.shfirstplayground.domain.model.MarketIndex
import com.babytigerdaddy.shfirstplayground.domain.repository.MarketIndexSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 야후파이낸스 무료 지수 — 앱키·로그인 없이 코스피/코스닥 지수 조회.
 *
 * 심볼: 코스피 `^KS11`, 코스닥 `^KQ11`(URL에선 ^를 %5E로). [YahooStockPriceSource]와 같은
 * v8 chart 엔드포인트를 쓴다. meta.regularMarketPrice(현재 지수)와 chartPreviousClose(전일 종가)로
 * 등락폭·등락률을 계산. 15~20분 지연.
 */
@Singleton
class YahooMarketIndexSource @Inject constructor() : MarketIndexSource {

    override suspend fun fetch(): List<MarketIndex> = withContext(Dispatchers.IO) {
        listOfNotNull(
            fetchOne("코스피", "%5EKS11"),
            fetchOne("코스닥", "%5EKQ11"),
        )
    }

    // 원/달러 환율(KRW=X). 지수와 같은 chart 엔드포인트라 파싱 그대로 재사용.
    override suspend fun fetchUsdKrw(): MarketIndex? = withContext(Dispatchers.IO) {
        fetchOne("원/달러", "KRW=X")
    }

    private fun fetchOne(name: String, symbol: String): MarketIndex? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL("$BASE$symbol?interval=1d&range=1d")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android) HoldingNote/1.0")
                connectTimeout = 7000
                readTimeout = 7000
            }
            if (conn.responseCode != 200) return null
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            parse(name, body)
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    /** chart.result[0].meta에서 현재값·전일종가 → 등락폭·등락률. */
    private fun parse(name: String, json: String): MarketIndex? {
        val meta = JSONObject(json)
            .optJSONObject("chart")
            ?.optJSONArray("result")
            ?.optJSONObject(0)
            ?.optJSONObject("meta")
            ?: return null
        val price = meta.optDouble("regularMarketPrice", Double.NaN)
        val prev = meta.optDouble("chartPreviousClose", meta.optDouble("previousClose", Double.NaN))
        if (price.isNaN() || price <= 0.0 || prev.isNaN() || prev <= 0.0) return null
        val change = price - prev
        val rate = change / prev * 100.0
        return MarketIndex(name = name, value = price, change = change, changeRate = rate)
    }

    private companion object {
        const val BASE = "https://query1.finance.yahoo.com/v8/finance/chart/"
    }
}
