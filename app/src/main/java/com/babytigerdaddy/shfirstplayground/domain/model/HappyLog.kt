package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.LocalDateTime

/**
 * 행복한 순간 한 단위 — 엄마가 5~7세 아이의 즐거운 시간을 짧게 남기는 엔트리.
 *
 * v4 핵심 도메인 모델. 위치·메모·사진·기분 4가지 데이터를 한 시점에 묶음.
 */
data class HappyLog(
    /** 안정 ID — UUID 또는 timestamp 기반. */
    val id: String,
    /** 기록 시점. */
    val occurredAt: LocalDateTime,
    /** 한 줄 메모 — "오늘 무엇이 행복했어요?". */
    val note: String,
    /** 기분 라벨 (Mood enum). */
    val mood: Mood,
    /** 위치 — null이면 위치 기록 안 함. */
    val location: Location? = null,
    /** 사진 파일 URI 리스트 — 기기 내부 저장. */
    val photoUris: List<String> = emptyList(),
)

/** 5~7세 아이의 기분 라벨 5종. */
enum class Mood {
    JOYFUL,
    EXCITED,
    CALM,
    PROUD,
    COZY,
}
