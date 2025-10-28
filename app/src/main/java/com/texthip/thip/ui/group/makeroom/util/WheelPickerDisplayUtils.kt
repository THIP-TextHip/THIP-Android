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
        // 시작 날짜: minDate와 (maxDate - 1일) 사이로 제한
        // (종료일과 최소 1일 간격을 보장하기 위해 maxDate보다 1일 작게 제한)
        val validatedStart = startDate.coerceIn(minDate, maxDate.minusDays(1))

        // 종료 날짜: (시작일 + 1일)과 maxDate 사이로 제한
        val minEndDate = validatedStart.plusDays(1)
        val validatedEnd = endDate.coerceIn(minEndDate, maxDate)

        return validatedStart to validatedEnd
    }
}
