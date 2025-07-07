package com.texthip.thip.ui.group.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.FloatingButton
import com.texthip.thip.ui.common.topappbar.LogoTopAppBar
import com.texthip.thip.ui.group.myroom.component.GroupMySectionHeader
import com.texthip.thip.ui.group.myroom.component.GroupPager
import com.texthip.thip.ui.group.myroom.component.GroupRoomDeadlineSection
import com.texthip.thip.ui.group.myroom.component.GroupSearchTextField
import com.texthip.thip.ui.group.myroom.mock.GroupViewModel
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors


/**
 * Displays the main group screen with sections for the user's groups and groups nearing their deadlines.
 *
 * Presents a vertically scrollable layout including a top app bar, search field, "My Groups" section, group cards, and a deadline section. User interactions are handled via the provided ViewModel.
 */
@Composable
fun GroupScreen(
    navController: NavHostController? = null,
    viewModel: GroupViewModel = viewModel()
) {
    val myGroups by viewModel.myGroups.collectAsState()
    val roomSections by viewModel.roomSections.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        Modifier
            .background(colors.Black)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 상단바
            LogoTopAppBar(
                leftIcon = painterResource(R.drawable.ic_done),
                hasNotification = true,
                onLeftClick = { },
                onRightClick = { }
            )
            Spacer(Modifier.height(16.dp))

            // 검색창
            GroupSearchTextField(onValueChange = { })
            Spacer(Modifier.height(32.dp))

            // 내 모임방 헤더 + 카드
            GroupMySectionHeader(
                onClick = { viewModel.onMyGroupHeaderClick() }
            )
            Spacer(Modifier.height(20.dp))

            GroupPager(
                groupCards = myGroups,
                onCardClick = { viewModel.onMyGroupCardClick(it) }
            )
            Spacer(Modifier.height(40.dp))

            Spacer(
                Modifier
                    .height(10.dp)
                    .fillMaxWidth()
                    .background(color = colors.DarkGrey02)
            )
            Spacer(Modifier.height(32.dp))

            // 마감 임박한 독서 모임방
            GroupRoomDeadlineSection(
                roomSections = roomSections,
                onRoomClick = { viewModel.onRoomCardClick(it) }
            )
            Spacer(Modifier.height(32.dp))
        }
        // 오른쪽 하단 FAB
        FloatingButton(
            icon = painterResource(id = R.drawable.ic_makegroup),
            onClick = { viewModel.onFabClick() }
        )
    }
}


@Preview()
@Composable
fun PreviewGroupScreen() {
    ThipTheme {
        val previewViewModel = remember { GroupViewModel() }
        GroupScreen(viewModel = previewViewModel)
    }
}