package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Displays a date range picker for selecting a group room duration within a constrained window.
 *
 * Allows the user to select a start date from today up to three months ahead. The end date is automatically set to one day after the selected start date and is read-only. The component enforces valid date selections, prevents choosing dates before today, and ensures the duration does not exceed 90 days. Informational and error messages are shown based on the current selection and user interaction.
 *
 * @param modifier Modifier for styling and layout.
 * @param onDateRangeSelected Callback invoked with the selected start and end dates whenever the selection changes.
 */
@Composable
fun GroupRoomDurationPicker(
    modifier: Modifier = Modifier,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit = { _, _ -> }
) {
    val today = LocalDate.now()
    val maxDate = today.plusMonths(3)

    // 날짜 상태
    var startYear by rememberSaveable { mutableStateOf(today.year) }
    var startMonth by rememberSaveable { mutableStateOf(today.monthValue) }
    var startDay by rememberSaveable { mutableStateOf(today.dayOfMonth) }

    // 유효한 날짜 범위 계산
    val years = remember { (today.year..maxDate.year).toList() }

    // 월 범위 계산 (년도에 따라 동적으로)
    val months = remember(startYear) {
        when (startYear) {
            today.year -> (today.monthValue..12).toList()
            maxDate.year -> (1..maxDate.monthValue).toList()
            else -> (1..12).toList()
        }
    }

    // 일 범위 계산 (년도, 월에 따라 동적으로)
    val days = remember(startYear, startMonth) {
        val selectedDate = LocalDate.of(startYear, startMonth, 1)
        val startDayOfMonth = if (startYear == today.year && startMonth == today.monthValue) {
            today.dayOfMonth
        } else {
            1
        }

        val endDayOfMonth = if (startYear == maxDate.year && startMonth == maxDate.monthValue) {
            minOf(selectedDate.lengthOfMonth(), maxDate.dayOfMonth)
        } else {
            selectedDate.lengthOfMonth()
        }

        (startDayOfMonth..endDayOfMonth).toList()
    }

    // 날짜 유효성 검사 및 자동 보정
    LaunchedEffect(startYear, startMonth, days) {
        if (startDay !in days) {
            startDay = days.lastOrNull() ?: startDay
        }
    }

    // 오늘 이전 날짜 선택 방지
    LaunchedEffect(startYear, startMonth, startDay) {
        val selectedDate = LocalDate.of(startYear, startMonth, startDay)
        if (selectedDate.isBefore(today)) {
            startYear = today.year
            startMonth = today.monthValue
            startDay = today.dayOfMonth
        }
    }

    // 날짜 객체로 변환
    val startDate = remember(startYear, startMonth, startDay) {
        try {
            LocalDate.of(startYear, startMonth, startDay)
        } catch (e: Exception) {
            // 유효하지 않은 날짜인 경우 오늘 날짜로 fallback
            today
        }
    }
    val endDate = remember(startDate) { startDate.plusDays(1) }

    // 90일 초과 체크
    val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
    val isOverLimit = daysBetween > 90

    var isPickerTouched by rememberSaveable { mutableStateOf(false) }

    // 날짜 변경 시 콜백
    LaunchedEffect(startDate, endDate) {
        onDateRangeSelected(startDate, endDate)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.group_room_duration_title),
            style = typography.smalltitle_sb600_s18_h24,
            color = colors.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 시작 날짜
            GroupDatePicker(
                year = startYear,
                month = startMonth,
                day = startDay,
                years = years,
                months = months,
                days = days,
                onYearSelected = { newYear ->
                    startYear = newYear
                    // 년도 변경 시 월 유효성 검사
                    val validMonths = when (newYear) {
                        today.year -> (today.monthValue..12).toList()
                        maxDate.year -> (1..maxDate.monthValue).toList()
                        else -> (1..12).toList()
                    }
                    if (startMonth !in validMonths) {
                        startMonth = validMonths.first()
                    }
                },
                onMonthSelected = { newMonth ->
                    startMonth = newMonth
                    // 월 변경 시 일 유효성 검사
                    val tempDate = LocalDate.of(startYear, newMonth, 1)
                    val validStartDay = if (startYear == today.year && newMonth == today.monthValue) {
                        today.dayOfMonth
                    } else {
                        1
                    }

                    val validEndDay = if (startYear == maxDate.year && newMonth == maxDate.monthValue) {
                        minOf(tempDate.lengthOfMonth(), maxDate.dayOfMonth)
                    } else {
                        tempDate.lengthOfMonth()
                    }

                    if (startDay < validStartDay) {
                        startDay = validStartDay
                    } else if (startDay > validEndDay) {
                        startDay = validEndDay
                    }
                },
                onDaySelected = { startDay = it },
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPickerTouched = true }
                    )
                }
            )
            // 구분자
            Text(
                text = "~",
                style = typography.info_r400_s12,
                color = colors.White,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            // 종료 날짜(선택 불가, 읽기 전용)
            GroupDatePicker(
                year = endDate.year,
                month = endDate.monthValue,
                day = endDate.dayOfMonth,
                years = years, // 전체 년도 범위 제공
                months = (1..12).toList(), // 전체 월 범위 제공
                days = (1..LocalDate.of(endDate.year, endDate.monthValue, 1).lengthOfMonth()).toList(), // 해당 월의 전체 일 범위 제공
                onYearSelected = {},
                onMonthSelected = {},
                onDaySelected = {},
                modifier = Modifier // 비활성화 UI 추가 가능
            )
        }
        // 안내/에러 메시지
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            when {
                isOverLimit -> {
                    Text(
                        text = stringResource(R.string.group_room_duration_comment),
                        style = typography.info_r400_s12,
                        color = colors.Red,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                !isPickerTouched -> {
                    Text(
                        text = stringResource(R.string.group_room_duration_comment),
                        style = typography.info_r400_s12,
                        color = colors.NeonGreen,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                else -> {
                    Text(
                        text = "${startDate.monthValue}월 ${startDate.dayOfMonth}일 자정에 자동으로 모집 마감되고 활동이 가능합니다.",
                        style = typography.info_r400_s12,
                        color = colors.NeonGreen,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
        // 에러 메시지: 90일 초과
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isOverLimit) {
                Text(
                    text = stringResource(R.string.group_room_duration_error, daysBetween),
                    style = typography.info_r400_s12,
                    color = colors.Red,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Displays a preview of the GroupRoomDurationPicker component within the app theme.
 *
 * This preview allows interactive selection of a date range and prints the selected range to the console.
 */
@Preview(showBackground = true)
@Composable
fun MeetingDurationPickerPreview() {
    ThipTheme {
        GroupRoomDurationPicker { startDate, endDate ->
            println("Selected date range: $startDate to $endDate")
        }
    }
}