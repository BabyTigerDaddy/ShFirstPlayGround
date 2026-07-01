package com.babytigerdaddy.shfirstplayground.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class BusinessDaysTest {

    // 요일이 확실한 월요일을 앵커로 잡는다.
    private val monday: LocalDate =
        LocalDate.parse("2026-06-15").with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    @Test
    fun `같은 날은 0`() {
        assertEquals(0L, BusinessDays.between(monday, monday))
    }

    @Test
    fun `월요일에서 금요일까지는 4거래일`() {
        // 화·수·목·금 = 4
        assertEquals(4L, BusinessDays.between(monday, monday.plusDays(4)))
    }

    @Test
    fun `금요일에 사서 월요일에 보면 1거래일 (주말 스킵)`() {
        val friday = monday.plusDays(4)
        val nextMonday = monday.plusDays(7)
        // 토·일 스킵 → 월요일 하루
        assertEquals(1L, BusinessDays.between(friday, nextMonday))
    }

    @Test
    fun `토요일에서 일요일은 0`() {
        val saturday = monday.plusDays(5)
        val sunday = monday.plusDays(6)
        assertEquals(0L, BusinessDays.between(saturday, sunday))
    }

    @Test
    fun `정확히 1주 뒤는 요일 무관 5거래일`() {
        assertEquals(5L, BusinessDays.between(monday, monday.plusWeeks(1)))
        assertEquals(10L, BusinessDays.between(monday, monday.plusWeeks(2)))
    }

    @Test
    fun `역순이면 0`() {
        assertEquals(0L, BusinessDays.between(monday.plusDays(4), monday))
    }
}
