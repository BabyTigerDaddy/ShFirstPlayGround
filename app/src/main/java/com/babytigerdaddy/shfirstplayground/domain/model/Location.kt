package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * GPS 좌표 + 옵션 사용자 label. v4 첫 commit은 manual entry, 다음 commit에서 자동 감지.
 *
 * 위치 데이터는 기기 내부에만 저장 — 서버 전송·외부 공유 X (5~7세 아동 정보 보호).
 */
data class Location(
    /** 위도. -90 ~ 90. */
    val latitude: Double,
    /** 경도. -180 ~ 180. */
    val longitude: Double,
    /** 사용자 입력 또는 reverse geocoding 결과 짧은 이름 — e.g. "동네 놀이터". */
    val label: String? = null,
)
