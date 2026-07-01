package com.babytigerdaddy.shfirstplayground.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 거래일(영업일) 계산 — 주식은 토·일에 장이 안 서므로 보유일을 주말 빼고 센다.
 *
 * 예) 금요일 편입 → 월요일 = 1거래일(토·일 스킵). 편입 당일은 0.
 */
object BusinessDays {

    /** [from] 다음날부터 [to]까지 주말(토·일) 제외한 일수. to <= from이면 0. */
    fun between(from: LocalDate, to: LocalDate): Long {
        if (!to.isAfter(from)) return 0
        var count = 0L
        var d = from.plusDays(1)
        while (!d.isAfter(to)) {
            if (d.dayOfWeek != DayOfWeek.SATURDAY && d.dayOfWeek != DayOfWeek.SUNDAY) count++
            d = d.plusDays(1)
        }
        return count
    }
}
