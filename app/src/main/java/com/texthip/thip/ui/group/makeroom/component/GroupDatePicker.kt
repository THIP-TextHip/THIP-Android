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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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

/**
 * Displays a horizontal date picker with wheel selectors for year, month, and day.
 *
 * Users can select a year, month, and day from the provided lists. The currently selected values are highlighted, and callbacks are invoked when a new value is chosen for each component.
 */
@Composable
fun GroupDatePicker(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    day: Int,
    years: List<Int>,
    months: List<Int>,
    days: List<Int>,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDaySelected: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 년도 선택기
        GroupWheelPicker(
            modifier = Modifier.width(48.dp),
            items = years,
            selectedItem = year,
            onItemSelected = onYearSelected,
            displayText = { it.toString() }
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = stringResource(R.string.group_year),
            style = typography.info_r400_s12,
            color = colors.White
        )
        Spacer(modifier = Modifier.width(4.dp))

        // 월 선택기
        GroupWheelPicker(
            modifier = Modifier.width(32.dp),
            items = months,
            selectedItem = month,
            onItemSelected = onMonthSelected,
            displayText = { it.toString() }
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = stringResource(R.string.group_month),
            style = typography.info_r400_s12,
            color = colors.White
        )
        Spacer(modifier = Modifier.width(4.dp))

        // 일 선택기
        GroupWheelPicker(
            modifier = Modifier.width(32.dp),
            items = days,
            selectedItem = day,
            onItemSelected = onDaySelected,
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

/**
 * Displays a preview of the GroupDatePicker component with two independently managed date pickers for selecting start and end dates.
 *
 * Demonstrates usage of GroupDatePicker within a themed container, initializing start and end dates to today and tomorrow, and dynamically updating the valid days based on the selected year and month.
 */
@Preview(showBackground = true)
@Composable
fun DatePickerGroupPreview() {
    ThipTheme {
        val today = LocalDate.now()
        val years = (2020..2030).toList()
        val months = (1..12).toList()
        val getDaysInMonth = { year: Int, month: Int ->
            val date = LocalDate.of(year, month, 1)
            (1..date.lengthOfMonth()).toList()
        }

        // 각각 독립적으로 관리!
        var startYear by remember { mutableStateOf(today.year) }
        var startMonth by remember { mutableStateOf(today.monthValue) }
        var startDay by remember { mutableStateOf(today.dayOfMonth) }

        val tomorrow = today.plusDays(1)
        var endYear by remember { mutableStateOf(tomorrow.year) }
        var endMonth by remember { mutableStateOf(tomorrow.monthValue) }
        var endDay by remember { mutableStateOf(tomorrow.dayOfMonth) }

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
                    year = startYear,
                    month = startMonth,
                    day = startDay,
                    years = years,
                    months = months,
                    days = getDaysInMonth(startYear, startMonth),
                    onYearSelected = { startYear = it },
                    onMonthSelected = { startMonth = it },
                    onDaySelected = { startDay = it }
                )
                // 끝 날짜
                Text("끝 날짜", color = colors.White)
                GroupDatePicker(
                    year = endYear,
                    month = endMonth,
                    day = endDay,
                    years = years,
                    months = months,
                    days = getDaysInMonth(endYear, endMonth),
                    onYearSelected = { endYear = it },
                    onMonthSelected = { endMonth = it },
                    onDaySelected = { endDay = it }
                )
            }
        }
    }
}
