package com.texthip.thip.ui.group.makeroom.util

import java.time.LocalDate
import kotlin.math.roundToInt

object WheelPickerUtils {
    @JvmStatic
    fun getCircularIndex(index: Int, size: Int): Int {
        return ((index % size) + size) % size
    }

    @JvmStatic
    fun normalizeOffset(offset: Float, itemSpacing: Float, size: Int, circular: Boolean): Float {
        if (!circular) return offset
        val total = size * itemSpacing
        return ((offset % total) + total) % total
    }

    @JvmStatic
    fun offsetToIndex(
        offset: Float,
        itemSpacing: Float,
        size: Int,
        circular: Boolean
    ): Int {
        val normalized = if (circular) normalizeOffset(offset, itemSpacing, size, circular) else offset
        val centerIndex = (-normalized / itemSpacing).roundToInt()
        return if (circular) getCircularIndex(centerIndex, size)
        else centerIndex.coerceIn(0, size - 1)
    }

    @JvmStatic
    fun validateDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        minDate: LocalDate,
        maxDate: LocalDate
    ): Pair<LocalDate, LocalDate> {
        // 1. 시작 날짜 유효성 검사
        val validatedStart = when {
            startDate.isBefore(minDate) -> minDate
            startDate.isAfter(maxDate) -> maxDate
            else -> startDate
        }

        // 2. 종료 날짜 유효성 검사 (최소 1일 기간 보장)
        val minEndDate = validatedStart.plusDays(1)
        val validatedEnd = when {
            endDate.isAfter(maxDate) -> maxDate
            endDate.isBefore(minEndDate) -> minEndDate
            else -> endDate
        }

        return validatedStart to validatedEnd
    }
}
