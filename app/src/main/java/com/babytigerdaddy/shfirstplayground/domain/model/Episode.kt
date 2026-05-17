package com.babytigerdaddy.shfirstplayground.domain.model

/**
 * 시리즈 안의 한 에피소드. v2 단일 에피소드 deep-dive prototype 단위.
 *
 * v1 Content와 다른 점: 메타데이터 + 캐릭터 매칭 level이 아니라 plot 기반 큐레이션 단위.
 * 한 Episode는 사전 큐레이션된 EpisodeQuestion 5~7개를 가진다.
 */
data class Episode(
    /** 안정 ID. e.g. "bluey_s01e01". */
    val id: String,
    /** 시리즈 이름. e.g. "Bluey". */
    val seriesTitle: String,
    /** 시즌 번호 (1-based). */
    val seasonNumber: Int,
    /** 시즌 내 에피 번호 (1-based). */
    val episodeNumber: Int,
    /** 에피 제목. */
    val title: String,
    /** 부모가 시청 전 보는 컨텍스트 1~2문장. spoiler 없이 정서·발달 주제만. */
    val parentContext: String,
    /** 핵심 발달·정서 주제 1줄. e.g. "상상놀이에서 멈춤 신호 알아채기". */
    val coreTheme: String,
    /** 등장 캐릭터 (질문 reference용). */
    val characters: List<String>,
    /** 분 단위 시청 시간. */
    val durationMin: Int,
    /** 권장 연령. */
    val ageRange: IntRange = 3..7,
    /** 콘텐츠 소유권자 attribution. e.g. "© BBC / Ludo Studio". */
    val attribution: String,
)
