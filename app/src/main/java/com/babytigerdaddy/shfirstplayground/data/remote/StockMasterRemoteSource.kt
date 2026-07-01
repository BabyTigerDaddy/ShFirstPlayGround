package com.babytigerdaddy.shfirstplayground.data.remote

import com.babytigerdaddy.shfirstplayground.domain.model.StockMaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/** 원격에서 받은 종목 마스터 묶음. */
data class RemoteStockMaster(
    val version: String,
    val stocks: List<StockMaster>,
)

/**
 * 전종목 목록을 공개 주소(GitHub raw)에서 받아온다 — 키·인증 없이.
 *
 * 새 종목 상장 시 이 파일만 갱신하면 앱 업데이트 없이 다음 진입에 반영된다.
 */
@Singleton
class StockMasterRemoteSource @Inject constructor() {

    suspend fun fetchMaster(): RemoteStockMaster? = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(MASTER_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "HoldingNote/1.0")
                connectTimeout = 10000
                readTimeout = 15000
            }
            if (conn.responseCode != 200) return@withContext null
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            parse(body)
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun parse(json: String): RemoteStockMaster? {
        val root = JSONObject(json)
        val version = root.optString("version").ifBlank { return null }
        val arr = root.optJSONArray("stocks") ?: return null
        val stocks = ArrayList<StockMaster>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val code = o.optString("code")
            val name = o.optString("name")
            val market = o.optString("market")
            if (code.length == 6 && name.isNotBlank()) {
                stocks.add(StockMaster(code = code, name = name, market = market))
            }
        }
        return if (stocks.isEmpty()) null else RemoteStockMaster(version, stocks)
    }

    private companion object {
        const val MASTER_URL =
            "https://raw.githubusercontent.com/BabyTigerDaddy/ShFirstPlayGround/holding-note-app/stock_master.json"
    }
}
