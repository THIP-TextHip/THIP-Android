package com.texthip.thip.ui.group.myroom.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.ui.common.cards.CardItemRoom
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme.colors
import androidx.compose.foundation.lazy.items
import com.texthip.thip.ui.common.topappbar.DefaultTopAppBar
import com.texthip.thip.ui.group.myroom.mock.GroupCardItemRoomData
import com.texthip.thip.ui.group.myroom.component.GroupMyRoomFilterRow
import com.texthip.thip.ui.theme.ThipTheme

/**
 * Displays a screen listing group rooms with filter options for recruiting status.
 *
 * Shows a top app bar, filter toggles, and a scrollable list of group rooms. Users can filter the list to show only recruiting, only non-recruiting, or all rooms. Clicking a room card triggers the provided callback.
 *
 * @param allDataList The complete list of group room data to display and filter.
 * @param onCardClick Callback invoked when a room card is clicked, receiving the selected room data.
 */
@Composable
fun GroupMyScreen(
    allDataList: List<GroupCardItemRoomData>,
    onCardClick: (GroupCardItemRoomData) -> Unit = {}
) {
    var selectedStates by remember { mutableStateOf(booleanArrayOf(false, false)) }

    val filteredList = remember(selectedStates, allDataList) {
        if (selectedStates.all { !it } || selectedStates.all { it }) {
            allDataList
        } else if (selectedStates[0]) {
            allDataList.filter { !it.isRecruiting }
        } else if (selectedStates[1]) {
            allDataList.filter { it.isRecruiting }
        } else {
            allDataList
        }
    }

    Column(
        Modifier
            .background(colors.Black)
            .fillMaxSize()
    ) {
        DefaultTopAppBar(
            title = stringResource(R.string.my_group_room),
            onLeftClick = {},
        )
        Column(
            Modifier
                .background(colors.Black)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            GroupMyRoomFilterRow(
                selectedStates = selectedStates,
                onToggle = { idx ->
                    selectedStates = selectedStates.copyOf().also { it[idx] = !it[idx] }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(filteredList) { item ->
                    CardItemRoom(
                        title = item.title,
                        participants = item.participants,
                        maxParticipants = item.maxParticipants,
                        isRecruiting = item.isRecruiting,
                        endDate = item.endDate,
                        imageRes = item.imageRes,
                        onClick = { onCardClick(item) }
                    )
                }
            }
        }
    }
}


@Preview()
@Composable
fun MyGroupListFilterScreenPreview() {
    ThipTheme {
        val dataList = listOf(
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 30,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 1,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 30,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 1,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 30,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 1,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "모임방 이름입니다. 모임방...",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 3,
                genreIndex = 0
            )
        )
        GroupMyScreen(allDataList = dataList)
    }
}