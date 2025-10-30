package com.texthip.thip.ui.feed.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.data.model.feed.response.AllFeedItem
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

@Composable
fun RecommendedFeedCarousel(
    recommendedFeeds: List<AllFeedItem>,
    onFeedClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendedFeeds.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.recommended_feeds_title),
            style = typography.title_b700_s20_h24,
            color = colors.White,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.recommended_feeds_subtitle),
            style = typography.copy_m500_s14_h20,
            color = colors.Grey,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 카드 캐러셀 부분
        if (recommendedFeeds.size == 1) {
            // 카드가 1개
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                RecommendFeedCard(
                    feedItem = recommendedFeeds[0],
                    onClick = { onFeedClick(recommendedFeeds[0].feedId.toLong()) }
                )
            }
        } else {
            val pagerState = rememberPagerState(
                pageCount = { recommendedFeeds.size }
            )

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                pageSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Box(
                    modifier = Modifier
                ) {
                    RecommendFeedCard(
                        feedItem = recommendedFeeds[page],
                        onClick = { onFeedClick(recommendedFeeds[page].feedId.toLong()) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecommendedFeedCarouselPreview() {
    ThipTheme {
        RecommendedFeedCarousel(
            recommendedFeeds = listOf(
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "user.01",
                    creatorProfileImageUrl = null,
                    aliasName = "공식 인플루언서",
                    aliasColor = "#97E4A3",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = false,
                    isWriter = false
                ),
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "user.01",
                    creatorProfileImageUrl = null,
                    aliasName = "공식 인플루언서",
                    aliasColor = "#97E4A3",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = false,
                    isWriter = false
                ),
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "user.01",
                    creatorProfileImageUrl = null,
                    aliasName = "공식 인플루언서",
                    aliasColor = "#97E4A3",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = false,
                    isWriter = false
                ),
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "user.01",
                    creatorProfileImageUrl = null,
                    aliasName = "공식 인플루언서",
                    aliasColor = "#97E4A3",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = false,
                    isWriter = false
                ),
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "user.01",
                    creatorProfileImageUrl = null,
                    aliasName = "공식 인플루언서",
                    aliasColor = "#97E4A3",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = false,
                    isWriter = false
                )
            ),
            onFeedClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecommendedFeedCarouselSinglePreview() {
    ThipTheme {
        RecommendedFeedCarousel(
            recommendedFeeds = listOf(
                AllFeedItem(
                    feedId = 1,
                    creatorId = 123L,
                    creatorNickname = "책읽는사람",
                    creatorProfileImageUrl = null,
                    aliasName = "문학 애호가",
                    aliasColor = "#FF6B9D",
                    postDate = "2시간 전",
                    isbn = "9788983711892",
                    bookTitle = "코스모스",
                    bookAuthor = "칼 세이건",
                    contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다.",
                    contentUrls = emptyList(),
                    likeCount = 42,
                    commentCount = 8,
                    isSaved = false,
                    isLiked = true,
                    isWriter = false
                )
            ),
            onFeedClick = {}
        )
    }
}
