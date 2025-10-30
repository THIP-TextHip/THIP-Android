package com.texthip.thip.ui.feed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.texthip.thip.R
import com.texthip.thip.data.model.feed.response.AllFeedItem
import com.texthip.thip.ui.common.buttons.ActionBarButton
import com.texthip.thip.ui.common.buttons.ActionBookButton
import com.texthip.thip.ui.common.header.ProfileBar
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import com.texthip.thip.utils.color.hexToColor

@Composable
fun RecommendFeedCard(
    feedItem: AllFeedItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colors.DarkGrey02
        )
    ) {
        val hasImages = feedItem.contentUrls.isNotEmpty()
        val maxTextLines = 3

        var isTextTruncated by remember { mutableStateOf(false) }

        // 추천글은 항상 3줄로 고정
        val processedText = remember(feedItem.contentBody) {
            val lines = feedItem.contentBody.split("\n")
            if (lines.size <= maxTextLines) {
                feedItem.contentBody
            } else {
                lines.take(maxTextLines).joinToString("\n")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            ProfileBar(
                profileImage = feedItem.creatorProfileImageUrl ?: "",
                topText = feedItem.creatorNickname,
                bottomText = feedItem.aliasName,
                bottomTextColor = hexToColor(feedItem.aliasColor),
                showSubscriberInfo = false,
                hoursAgo = feedItem.postDate,
                onClick = onClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                ActionBookButton(
                    bookTitle = feedItem.bookTitle,
                    bookAuthor = feedItem.bookAuthor,
                    onClick = onClick
                )
            }

            Column(
                modifier = Modifier.clickable { onClick() },
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

                    // 텍스트가 잘린 경우에
                    if (isTextTruncated) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_text_more_darkgrey),
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
                        feedItem.contentUrls.take(3).forEach { imageUrl ->
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
                isSaveVisible = false,
                isSaved = feedItem.isSaved,
                isLockIcon = false,
                onLikeClick = { /* 카드 전체 클릭만 처리 */ },
                onCommentClick = onClick,
                onBookmarkClick = { /* 카드 전체 클릭만 처리 */ }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecommendedFeedCardPreview() {
    ThipTheme {
        RecommendFeedCard(
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
                contentBody = "이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요. 이 책을 읽으면서 우주에 대한 새로운 시각을 갖게 되었습니다. 과학적 사실들이 아름다운 문장으로 표현되어 있어서 읽는 내내 감동받았어요.",
                contentUrls = emptyList(),
                likeCount = 42,
                commentCount = 8,
                isSaved = false,
                isLiked = false,
                isWriter = false
            ),
            onClick = {}
        )
    }
}