package com.texthip.thip.ui.group.myroom.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.group.myroom.mock.GroupCardData
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors

/**
 * Displays a horizontally scrollable pager of group cards with dynamic sizing and spacing.
 *
 * Each card scales and changes opacity based on its selection state, and a pager indicator shows the current page.
 *
 * @param groupCards The list of group card data to display in the pager.
 * @param onCardClick Callback invoked when a group card is clicked.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GroupPager(
    groupCards: List<GroupCardData>,
    onCardClick: (GroupCardData) -> Unit
) {
    val scale = 0.86f
    val desiredGap = 10.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
    ) {
        val horizontalPadding = 30.dp
        val cardWidth = maxWidth - (horizontalPadding * 2)

        val pageSpacing = with(this) {
            (-(cardWidth - (cardWidth * scale)) / 2f) + desiredGap
        }

        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { maxOf(1, groupCards.size) }
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(start = horizontalPadding, end = horizontalPadding),
            pageSpacing = pageSpacing,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val isCurrent = pagerState.currentPage == page
            val scale = if (isCurrent) 1f else 0.86f
            val bgColor = if (isCurrent) colors.White else colors.DarkGrey

            Box(
                modifier = Modifier
                    .width(cardWidth)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = if (isCurrent) 1f else 0.7f
                    }
            ) {
                GroupMainCard(
                    data = groupCards[page],
                    onClick = { onCardClick(groupCards[page]) },
                    backgroundColor = bgColor
                )
            }
        }

        SimplePagerIndicator(
            pageCount = groupCards.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 12.dp)
        )
    }
}

@Preview()
@Composable
fun PreviewMyGroupPager() {
    ThipTheme {
        val list = listOf(
            GroupCardData(
                title = "호르몬 체인지 완독하는 방",
                members = 22,
                imageRes = R.drawable.bookcover_sample,
                progress = 40,
                nickname = "uibowl1님"
            ),
            GroupCardData(
                title = "명작 읽기방",
                members = 10,
                imageRes = R.drawable.bookcover_sample,
                progress = 70,
                nickname = "joyce님"
            ),
            GroupCardData(
                title = "또 다른 방",
                members = 13,
                imageRes = R.drawable.bookcover_sample,
                progress = 10,
                nickname = "other님"
            )
        )
        GroupPager(groupCards = list, onCardClick = {})
    }
}
