package com.texthip.thip.ui.group.myroom.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.GenreChipRow
import com.texthip.thip.ui.common.cards.CardItemRoom
import com.texthip.thip.ui.group.myroom.mock.GroupRoomSectionData
import com.texthip.thip.ui.group.myroom.mock.GroupCardItemRoomData
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays a horizontally scrollable pager of grouped room sections, each with genre filtering and up to three room cards.
 *
 * Each page represents a room section with a title, selectable genre chips, and a filtered list of room cards. Cards are visually scaled based on selection, and the pager layout dynamically adjusts to the container width. Invokes a callback when a room card is clicked.
 *
 * @param roomSections The list of room sections to display, each containing genres and associated rooms.
 * @param onRoomClick Callback invoked when a room card is selected.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GroupRoomDeadlineSection(
    roomSections: List<GroupRoomSectionData>,
    onRoomClick: (GroupCardItemRoomData) -> Unit
) {
    val sideMargin = 30.dp

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { roomSections.size }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(588.dp),
            contentAlignment = Alignment.Center
        ) {
            val horizontalPadding = sideMargin
            val cardWidth = maxWidth - (horizontalPadding * 2)
            val scale = 0.9f
            val desiredGap = 12.dp // TODO: 이 부분을 10dp로 하면 양 옆의 카드에 살짝 다음 내용이 보여서 12정도가 어떤지 

            val pageSpacing = (-(cardWidth - (cardWidth * scale)) / 2) + desiredGap

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 30.dp),
                pageSpacing = pageSpacing,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val section = roomSections[page]
                var selectedGenre by remember { mutableIntStateOf(0) }

                val isCurrent = pagerState.currentPage == page
                val scale = if (isCurrent) 1f else 0.9f

                Box(
                    modifier = Modifier
                        .width(cardWidth)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    colors.White.copy(0.25f),
                                    colors.Black.copy(0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = section.title,
                            style = typography.title_b700_s20_h24,
                            color = colors.White
                        )
                        Spacer(Modifier.height(40.dp))

                        GenreChipRow(
                            genres = section.genres,
                            selectedIndex = selectedGenre,
                            onSelect = { idx -> selectedGenre = idx }
                        )
                        Spacer(Modifier.height(20.dp))

                        val cards = section.rooms.filter { it.genreIndex == selectedGenre }.take(3)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cards.forEach { room ->
                                CardItemRoom(
                                    title = room.title,
                                    participants = room.participants,
                                    maxParticipants = room.maxParticipants,
                                    isRecruiting = room.isRecruiting,
                                    endDate = room.endDate,
                                    imageRes = room.imageRes,
                                    onClick = { onRoomClick(room) },
                                    hasBorder = true,
                                )
                            }
                            if (cards.size < 3) {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f, fill = true)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays a preview of the GroupRoomDeadlineSection composable with sample room section data and genres.
 *
 * This preview includes three types of room sections: rooms with imminent deadlines, popular rooms, and influencer/author rooms, each populated with example data.
 */
@Preview()
@Composable
fun PreviewGroupRoomPagerSection() {
    ThipTheme {
        val genres = listOf("문학", "과학·IT", "사회과학", "인문학", "예술")

        // 마감 임박한 독서 모임방
        val deadlineRooms = listOf(
            GroupCardItemRoomData(
                title = "시집만 읽는 사람들 3월",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "일본 소설 좋아하는 사람들",
                participants = 15,
                maxParticipants = 20,
                isRecruiting = true,
                endDate = 2,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "명작 같이 읽기방",
                participants = 22,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 3,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "물리책 읽는 방",
                participants = 13,
                maxParticipants = 20,
                isRecruiting = true,
                endDate = 1,
                genreIndex = 1
            )
        )

        // 인기 있는 독서 모임방
        val popularRooms = listOf(
            GroupCardItemRoomData(
                title = "베스트셀러 토론방",
                participants = 28,
                maxParticipants = 30,
                isRecruiting = true,
                endDate = 7,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "인기 소설 완독방",
                participants = 25,
                maxParticipants = 25,
                isRecruiting = false,
                endDate = 5,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "트렌드 과학서 읽기",
                participants = 20,
                maxParticipants = 25,
                isRecruiting = true,
                endDate = 10,
                genreIndex = 1
            )
        )

        // 인플루언서, 작가 독서 모임방
        val influencerRooms = listOf(
            GroupCardItemRoomData(
                title = "작가와 함께하는 독서방",
                participants = 30,
                maxParticipants = 30,
                isRecruiting = false,
                endDate = 14,
                genreIndex = 0
            ),
            GroupCardItemRoomData(
                title = "유명 북튜버와 읽기",
                participants = 18,
                maxParticipants = 20,
                isRecruiting = true,
                endDate = 8,
                genreIndex = 2
            ),
            GroupCardItemRoomData(
                title = "작가 초청 인문학방",
                participants = 15,
                maxParticipants = 20,
                isRecruiting = true,
                endDate = 12,
                genreIndex = 3
            )
        )

        val roomSections = listOf(
            GroupRoomSectionData(
                title = stringResource(R.string.deadline_string),
                rooms = deadlineRooms,
                genres = genres
            ),
            GroupRoomSectionData(
                title = "인기 있는 독서 모임방",
                rooms = popularRooms,
                genres = genres
            ),
            GroupRoomSectionData(
                title = "인플루언서·작가 독서 모임방",
                rooms = influencerRooms,
                genres = genres
            )
        )

        GroupRoomDeadlineSection(
            roomSections = roomSections,
            onRoomClick = {}
        )
    }
}