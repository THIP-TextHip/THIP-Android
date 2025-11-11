package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import java.time.LocalDate

@Composable
fun GroupDatePicker(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // 선택된 날짜에서 년/월/일 추출
    val year = selectedDate.year
    val month = selectedDate.monthValue
    val day = selectedDate.dayOfMonth

    // 유효한 범위 계산 - 단순하게 전체 범위 사용
    val years = remember(minDate.year, maxDate.year) {
        (minDate.year..maxDate.year).toList()
    }
    val months = remember { (1..12).toList() }
    val days = remember(year, month) {
        (1..LocalDate.of(year, month, 1).lengthOfMonth()).toList()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupWheelPicker(
                modifier = modifier.width(48.dp),
                items = years,
                selectedItem = year,
                onItemSelected = { newYear ->
                    val newDate = try {
                        LocalDate.of(newYear, month, day)
                    } catch (e: Exception) {
                        val lastDay = LocalDate.of(newYear, month, 1).lengthOfMonth()
                        LocalDate.of(newYear, month, lastDay)
                    }
                    onDateSelected(newDate)
                },
                displayText = { it.toString() }
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(R.string.group_year),
                style = typography.info_r400_s12,
                color = colors.White
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupWheelPicker(
                modifier = Modifier.width(32.dp),
                items = months,
                selectedItem = month,
                onItemSelected = { newMonth ->
                    val newDate = try {
                        LocalDate.of(year, newMonth, day)
                    } catch (e: Exception) {
                        val lastDay = LocalDate.of(year, newMonth, 1).lengthOfMonth()
                        LocalDate.of(year, newMonth, lastDay)
                    }
                    onDateSelected(newDate)
                },
                displayText = { it.toString() }
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(R.string.group_month),
                style = typography.info_r400_s12,
                color = colors.White
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupWheelPicker(
                modifier = Modifier.width(32.dp),
                items = days,
                selectedItem = day.coerceAtMost(days.maxOrNull() ?: 1),
                onItemSelected = { newDay ->
                    onDateSelected(LocalDate.of(year, month, newDay))
                },
                displayText = { it.toString() }
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.group_day),
                style = typography.info_r400_s12,
                color = colors.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerGroupPreview() {
    ThipTheme {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val maxDate = today.plusMonths(12)

        var startDate by remember { mutableStateOf(today) }
        var endDate by remember { mutableStateOf(tomorrow) }

        Box(
            modifier = Modifier
                .background(colors.Black)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 시작 날짜
                Text("시작 날짜", color = colors.White)
                GroupDatePicker(
                    selectedDate = startDate,
                    minDate = today,
                    maxDate = maxDate,
                    onDateSelected = { newDate ->
                        startDate = newDate
                    }
                )

                // 끝 날짜
                Text("끝 날짜", color = colors.White)
                GroupDatePicker(
                    selectedDate = endDate,
                    minDate = today,
                    maxDate = maxDate,
                    onDateSelected = { newDate ->
                        endDate = newDate
                    }
                )
            }
        }
    }
}