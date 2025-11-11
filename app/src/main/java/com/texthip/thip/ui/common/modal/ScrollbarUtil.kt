package com.texthip.thip.ui.common.modal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.drawVerticalScrollbar(
    scrollState: ScrollState,
    trackThickness: Dp = 1.dp,
    thumbThickness: Dp = 3.dp,
    trackColor: Color = Color.White.copy(alpha = 0.2f),
    thumbColor: Color = Color.White.copy(alpha = 0.8f),
): Modifier = this.then(
    Modifier.drawBehind {
        if (scrollState.maxValue == 0) return@drawBehind

        val scrollbarHeight = size.height / 8f
        val scrollProgress = scrollState.value.toFloat() / scrollState.maxValue
        val scrollbarOffsetY = (size.height - scrollbarHeight) * scrollProgress

        //전체 고정 바
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(x = size.width - trackThickness.toPx(), y = 0f),
            size = Size(trackThickness.toPx(), size.height),
            cornerRadius = CornerRadius(trackThickness.toPx() / 2)
        )
        //핸들 바
        drawRoundRect(
            color = thumbColor,
            topLeft = Offset(x = size.width - thumbThickness.toPx(), y = scrollbarOffsetY),
            size = Size(thumbThickness.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(thumbThickness.toPx() / 2)
        )
    }
)

fun Modifier.drawVerticalScrollbar(
    lazyListState: LazyListState,
    trackThickness: Dp = 1.dp,
    thumbThickness: Dp = 3.dp,
    trackColor: Color = Color.White.copy(alpha = 0.2f),
    thumbColor: Color = Color.White.copy(alpha = 0.8f),
): Modifier = this.then(
    Modifier.drawBehind {
        val layoutInfo = lazyListState.layoutInfo
        val totalItemsCount = layoutInfo.totalItemsCount
        val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset

        // 스크롤이 필요 없으면 스크롤바 표시 안 함
        if (totalItemsCount == 0) return@drawBehind
        if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) return@drawBehind

        val scrollbarHeight = size.height / 8f

        // 전체 컨텐츠 높이 추정
        val averageItemHeight = if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
            layoutInfo.visibleItemsInfo.map { it.size }.average().toFloat()
        } else {
            100f
        }
        val estimatedTotalHeight = averageItemHeight * totalItemsCount

        // 스크롤 진행률 계산
        val firstVisibleIndex = lazyListState.firstVisibleItemIndex.toFloat()
        val firstVisibleOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()

        val scrollProgress = if (estimatedTotalHeight > 0) {
            ((firstVisibleIndex * averageItemHeight + firstVisibleOffset) / estimatedTotalHeight)
                .coerceIn(0f, 1f)
        } else {
            0f
        }

        val scrollbarOffsetY = (size.height - scrollbarHeight) * scrollProgress

        //전체 고정 바
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(x = size.width - trackThickness.toPx(), y = 0f),
            size = Size(trackThickness.toPx(), size.height),
            cornerRadius = CornerRadius(trackThickness.toPx() / 2)
        )
        //핸들 바
        drawRoundRect(
            color = thumbColor,
            topLeft = Offset(x = size.width - thumbThickness.toPx(), y = scrollbarOffsetY),
            size = Size(thumbThickness.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(thumbThickness.toPx() / 2)
        )
    }
)