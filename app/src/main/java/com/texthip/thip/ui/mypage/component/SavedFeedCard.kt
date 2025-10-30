package com.texthip.thip.ui.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.ActionBarButton
import com.texthip.thip.ui.common.buttons.ActionBookButton
import com.texthip.thip.ui.common.header.ProfileBar
import com.texthip.thip.ui.mypage.mock.FeedItem
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

@Composable
fun SavedFeedCard(
    modifier: Modifier = Modifier,
    feedItem: FeedItem,
    bottomTextColor: Color = colors.NeonGreen,
    onBookmarkClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onContentClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val hasImages = feedItem.imageUrls.isNotEmpty()
    val maxTextLines = if (hasImages) 3 else 8

    // 실제 텍스트 줄 수를 기준으로 표시할 텍스트 계산
    val processedText = remember(feedItem.content, hasImages) {
        val lines = feedItem.content.split("\n")
        val nonEmptyLines = mutableListOf<Int>() // 실제 텍스트가 있는 줄의 인덱스

        lines.forEachIndexed { index, line ->
            if (line.trim().isNotEmpty()) {
                nonEmptyLines.add(index)
            }
        }

        if (nonEmptyLines.size <= maxTextLines) {
            // 실제 텍스트 줄이 제한보다 적으면 전체 표시
            feedItem.content
        } else {
            // 실제 텍스트 줄이 제한을 초과하면, maxTextLines 번째 텍스트 줄까지만 표시
            val lastAllowedLineIndex = nonEmptyLines[maxTextLines - 1]
            lines.take(lastAllowedLineIndex + 1).joinToString("\n")
        }
    }

    var isTextTruncated by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        ProfileBar(
            profileImage = feedItem.userProfileImage ?: "https://example.com/image1.jpg",
            topText = feedItem.userName,
            bottomText = feedItem.userRole,
            bottomTextColor = bottomTextColor,
            showSubscriberInfo = false,
            hoursAgo = feedItem.timeAgo,
            onClick = onProfileClick
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            ActionBookButton(
                bookTitle = feedItem.bookTitle,
                bookAuthor = feedItem.authName,
                onClick = onBookClick
            )
        }

        Column(
            modifier = Modifier
                .clickable { onContentClick() }, // 전체 영역 클릭 유지
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Text(
                    text = processedText,
                    style = typography.feedcopy_r400_s14_h20,
                    color = colors.White,
                    maxLines = maxTextLines,
                    modifier = Modifier.fillMaxWidth(),
                    onTextLayout = { textLayoutResult ->
                        isTextTruncated = textLayoutResult.hasVisualOverflow
                    }
                )

                // 텍스트가 잘린 경우에만 "...더보기" 표시
                if (isTextTruncated) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_text_more),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            if (hasImages) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    feedItem.imageUrls.take(3).forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        ActionBarButton(
            modifier = Modifier.padding(top = 16.dp),
            isLiked = feedItem.isLiked,
            likeCount = feedItem.likeCount,
            commentCount = feedItem.commentCount,
            isSaveVisible = true,
            isSaved = feedItem.isSaved,
            isLockIcon = feedItem.isLocked,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onBookmarkClick = onBookmarkClick
        )
    }
}

@Preview
@Composable
private fun SavedFeedCardPrev() {
    val feed1 = FeedItem(
        id = 1L,
        userProfileImage = "https://example.com/profile1.jpg",
        userName = "user.01",
        userRole = stringResource(R.string.influencer),
        bookTitle = "책 제목",
        authName = "한강",
        timeAgo = "3시간 전",
        content = "무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷무한대로입력가능합니닷",
        likeCount = 10,
        commentCount = 5,
        isLiked = false,
        isSaved = true,
        imageUrls = emptyList()
    )

    val feed2 = FeedItem(
        id = 2L,
        userProfileImage = "https://example.com/profile2.jpg",
        userName = "user.01",
        userRole = stringResource(R.string.influencer),
        bookTitle = "책 제목",
        authName = "한강",
        timeAgo = "3시간 전",
        content = "한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 " +
                "한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능한줄만 입력 가능",
        likeCount = 10,
        commentCount = 5,
        isLiked = false,
        isSaved = true,
        imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        )
    )
    val scrollState = rememberScrollState()

    ThipTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            SavedFeedCard(
                feedItem = feed1
            )
            SavedFeedCard(
                feedItem = feed2
            )
        }
    }
}