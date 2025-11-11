package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun GroupMemberLimitPicker(
    modifier: Modifier = Modifier,
    selectedCount: Int = 30,
    onCountSelected: (Int) -> Unit = { }
) {
    val memberCounts = remember { (2..30).toList() }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 제목
        Text(
            text = stringResource(R.string.group_room_member_limit_title),
            style = typography.smalltitle_sb600_s18_h24,
            color = colors.White
        )

        // 인원 선택기
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 숫자 선택기
            GroupWheelPicker(
                modifier = Modifier.width(32.dp),
                items = memberCounts,
                selectedItem = selectedCount,
                onItemSelected = onCountSelected,
                displayText = { it.toString() }
            )

            // 단위 텍스트
            Text(
                text = stringResource(R.string.group_room_limit),
                style = typography.info_r400_s12,
                color = colors.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberLimitPickerPreview() {
    ThipTheme {
        var selectedCount by remember { mutableIntStateOf(30) }

        GroupMemberLimitPicker(
            selectedCount = selectedCount,
            onCountSelected = { selectedCount = it }
        )
    }
}