package com.texthip.thip.ui.group.myroom.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.group.myroom.mock.GroupCardData
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays a card representing a group reading room with book cover, title, participant count, user nickname, and progress.
 *
 * The card features a linear gradient background, rounded corners, and is fully clickable. It visually presents group details and a progress bar indicating the user's reading completion percentage.
 *
 * @param data The group card data containing title, image resource, participant count, nickname, and progress.
 * @param backgroundColor The background color of the card. Defaults to white.
 * @param onClick Callback invoked when the card is clicked.
 */
@Composable
fun GroupMainCard(
    data: GroupCardData,
    backgroundColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    // 그라데이션
    val gradient = Brush.linearGradient(
        colors = listOf(
            colors.White,
            colors.Grey01
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(brush = gradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp, top = 34.dp, bottom = 34.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 책 이미지
                Image(
                    painter = painterResource(id = data.imageRes),
                    contentDescription = "책 이미지",
                    modifier = Modifier
                        .size(width = 80.dp, height = 107.dp)
                )
                Spacer(Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.height(2.dp))
                    // 제목
                    Text(
                        text = data.title,
                        style = typography.smalltitle_sb600_s18_h24,
                        color = colors.Black,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    // 인원
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(2.dp))

                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.group_participant, data.members),
                                color = colors.Grey02,
                                style = typography.menu_sb600_s12,
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = stringResource(R.string.group_participant_string),
                                color = colors.Grey02,
                                style = typography.info_m500_s12,
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    // 닉네임 + 진행도
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(R.string.group_progress, data.nickname),
                            color = colors.Grey02,
                            style = typography.view_m500_s14
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${data.progress}%",
                            color = colors.Purple,
                            style = typography.view_m500_s14,
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    val percentage = data.progress.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(color = colors.Grey02, shape = RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = percentage / 100f)
                                .fillMaxHeight()
                                .background(color = colors.Purple, shape = RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}


@Preview()
@Composable
fun PreviewMyGroupMainCard() {
    ThipTheme {
        GroupMainCard(
            data = GroupCardData(
                title = "호르몬 체인지 완독하는 방",
                members = 22,
                imageRes = R.drawable.bookcover_sample,
                progress = 42,
                nickname = "uibowl"
            ),
            onClick = {}
        )
    }
}