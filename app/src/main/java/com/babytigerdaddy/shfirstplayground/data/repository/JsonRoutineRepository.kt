package com.babytigerdaddy.shfirstplayground.data.repository

import android.content.Context
import com.babytigerdaddy.shfirstplayground.domain.model.Routine
import com.babytigerdaddy.shfirstplayground.domain.model.RoutineStep
import com.babytigerdaddy.shfirstplayground.domain.repository.RoutineRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * assets/routines.json 정적 라우틴 템플릿 로더.
 * Phase 1은 morning + evening 2개. 사용자 커스터마이즈는 Phase 2.
 */
@Singleton
class JsonRoutineRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : RoutineRepository {

    @Volatile
    private var cache: List<Routine>? = null

    private suspend fun load(): List<Routine> = withContext(Dispatchers.IO) {
        cache ?: parse().also { cache = it }
    }

    private fun parse(): List<Routine> {
        val raw = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val arr = root.getJSONArray("routines")
        return List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            val stepsArr = obj.getJSONArray("steps")
            val routineId = obj.getString("id")
            val steps = List(stepsArr.length()) { j ->
                val s = stepsArr.getJSONObject(j)
                RoutineStep(
                    id = s.getString("id"),
                    routineId = routineId,
                    title = s.getString("title"),
                    order = s.getInt("order"),
                )
            }
            Routine(
                id = routineId,
                title = obj.getString("title"),
                displayOrder = obj.getInt("displayOrder"),
                steps = steps,
            )
        }.sortedBy { it.displayOrder }
    }

    override suspend fun listRoutines(): List<Routine> = load()

    override suspend fun getRoutine(id: String): Routine? = load().find { it.id == id }

    companion object {
        private const val ASSET_PATH = "routines.json"
    }
}
