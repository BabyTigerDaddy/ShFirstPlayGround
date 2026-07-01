package com.babytigerdaddy.shfirstplayground.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** 종목 마스터 로컬 버전 저장 — 원격 버전과 비교해 바뀌었을 때만 다시 받으려는 용도. */
@Singleton
class MasterVersionStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("stock_master", Context.MODE_PRIVATE)

    var version: String?
        get() = prefs.getString(KEY, null)
        set(value) { prefs.edit().putString(KEY, value).apply() }

    private companion object {
        const val KEY = "master_version"
    }
}
