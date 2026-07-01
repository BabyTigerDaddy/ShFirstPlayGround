package com.babytigerdaddy.shfirstplayground.ui.theme

import com.babytigerdaddy.shfirstplayground.data.local.ThemePreferenceStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** 현재 선택된 테마(모양·색). */
data class ThemeSettings(
    val shape: ThemeShape = ThemeShape.SOFT,
    val palette: ThemePalette = ThemePalette.LIGHT,
)

/**
 * 앱 전역 테마 상태 소유자.
 *
 * 저장된 선택을 읽어 [settings]로 흘려보내고, 세팅 화면에서 바꾸면
 * 즉시 반영 + 기기에 저장. MainActivity가 이걸 구독해 테마를 적용한다.
 */
@Singleton
class ThemeController @Inject constructor(
    private val store: ThemePreferenceStore,
) {
    private val _settings = MutableStateFlow(ThemeSettings(store.shape, store.palette))
    val settings: StateFlow<ThemeSettings> = _settings.asStateFlow()

    fun setShape(shape: ThemeShape) {
        store.shape = shape
        _settings.value = _settings.value.copy(shape = shape)
    }

    fun setPalette(palette: ThemePalette) {
        store.palette = palette
        _settings.value = _settings.value.copy(palette = palette)
    }
}
