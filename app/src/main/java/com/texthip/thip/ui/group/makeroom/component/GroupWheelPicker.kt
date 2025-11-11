package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.texthip.thip.ui.group.makeroom.util.WheelPickerUtils
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun <T> GroupWheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayText: (T) -> String = { it.toString() },
    selectedBackgroundColor: Color = colors.DarkGrey,
    itemHeight: Int = 20,
    isCircular: Boolean = true
) {
    if (items.isEmpty()) return

    val isScrollEnabled = items.size > 1
    val circular = isCircular && items.size > 2

    val selectedIndex = remember(items, selectedItem) { items.indexOf(selectedItem) }
    val animatableOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    var velocity by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val itemHeightPx = remember(density, itemHeight) { with(density) { itemHeight.dp.toPx() } }
    val spacingPx = remember(density) { with(density) { 9.dp.toPx() } }
    val itemSpacing = remember(itemHeightPx, spacingPx) { itemHeightPx + spacingPx }

    val getCircularIndex = remember(items.size) {
        { index: Int ->
            WheelPickerUtils.getCircularIndex(index, items.size)
        }
    }

    val normalizeOffset = remember(itemSpacing, items.size, circular) {
        { offset: Float ->
            WheelPickerUtils.normalizeOffset(offset, itemSpacing, items.size, circular)
        }
    }

    val offsetToIndex = remember(itemSpacing, items.size, circular) {
        { offset: Float ->
            WheelPickerUtils.offsetToIndex(offset, itemSpacing, items.size, circular)
        }
    }

    // 선택 아이템이 바뀌면 중앙에 오도록 offset 이동
    LaunchedEffect(selectedItem) {
        if (!isDragging && isScrollEnabled) {
            val targetOffset = -selectedIndex * itemSpacing
            animatableOffset.animateTo(
                if (circular) normalizeOffset(targetOffset) else targetOffset,
                animationSpec = spring()
            )
        }
    }

    // 드래그 상태가 변경될 때만 선택 아이템 업데이트
    LaunchedEffect(isDragging) {
        if (!isDragging && isScrollEnabled) {
            // 드래그가 끝났을 때만 최종 값 업데이트
            val newSelectedIndex = offsetToIndex(animatableOffset.value)
            if (newSelectedIndex in items.indices && items[newSelectedIndex] != selectedItem) {
                onItemSelected(items[newSelectedIndex])
            }
        }
    }

    val containerHeight = remember(itemHeight) { (itemHeight * 3 + 36).dp }
    val textStyle = typography.info_r400_s12

    Box(
        modifier = modifier.height(containerHeight)
    ) {
        // 중앙 고정 박스
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    selectedBackgroundColor,
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 9.dp)
        ) {
            Text(
                text = displayText(selectedItem),
                style = textStyle,
                color = Color.Transparent,
                textAlign = TextAlign.Center
            )
        }

        // 아이템들
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isScrollEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                    velocity = 0f
                                },
                                onDragEnd = {
                                    isDragging = false
                                    coroutineScope.launch {
                                        // 관성 스크롤
                                        if (abs(velocity) > 100f) {
                                            animatableOffset.animateDecay(
                                                initialVelocity = velocity,
                                                animationSpec = exponentialDecay(
                                                    frictionMultiplier = 0.9f,
                                                    absVelocityThreshold = 0.1f
                                                )
                                            )
                                        }

                                        // offset 스냅
                                        val currOffset = animatableOffset.value
                                        val normalized =
                                            if (circular) normalizeOffset(currOffset) else currOffset
                                        val snapIndex = (-normalized / itemSpacing).roundToInt()
                                        val snapOffset = -snapIndex * itemSpacing
                                        animatableOffset.animateTo(
                                            if (circular) normalizeOffset(snapOffset) else snapOffset,
                                            animationSpec = spring(
                                                dampingRatio = 0.8f,
                                                stiffness = 400f
                                            )
                                        )
                                    }
                                }
                            ) { _, dragAmount ->
                                velocity = dragAmount.y
                                coroutineScope.launch {
                                    val newOffset = animatableOffset.value + dragAmount.y
                                    if (circular) {
                                        animatableOffset.snapTo(normalizeOffset(newOffset))
                                    } else {
                                        val maxOffset = itemSpacing + spacingPx
                                        val minOffset =
                                            -(items.size - 1) * itemSpacing - itemSpacing - spacingPx
                                        animatableOffset.snapTo(
                                            newOffset.coerceIn(
                                                minOffset,
                                                maxOffset
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            val currOffset by remember {
                derivedStateOf {
                    if (circular && isScrollEnabled) normalizeOffset(animatableOffset.value) else animatableOffset.value
                }
            }
            val centerIndex by remember {
                derivedStateOf {
                    if (isScrollEnabled) (-currOffset / itemSpacing).roundToInt() else 0
                }
            }

            val visibleRange = remember(isScrollEnabled) { if (isScrollEnabled) -1..1 else 0..0 }

            visibleRange.forEach { relIdx ->
                val displayIndex = centerIndex + relIdx
                val actualIndex =
                    if (circular && isScrollEnabled) getCircularIndex(displayIndex) else {
                        if (displayIndex in 0 until items.size) displayIndex else return@forEach
                    }
                val item = items[actualIndex]
                val itemOffset =
                    if (isScrollEnabled) currOffset + (displayIndex * itemSpacing) else 0f
                val itemY = itemOffset + itemSpacing + spacingPx

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .offset { IntOffset(0, itemY.roundToInt()) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayText(item),
                        style = textStyle,
                        color = colors.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 그라데이션 오버레이 (아이템이 여러 개일 때만 표시)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.Black.copy(alpha = 0.8f),
                            Color.Transparent,
                            Color.Transparent,
                            colors.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WheelPickerPreview() {
    var selectedYear by remember { mutableStateOf(2025) }
    val years = (2020..2030).toList() // 11개

    var selectedSingleItem by remember { mutableStateOf("Only Item") }
    val singleItemList = listOf("Only Item") // 1개

    Box(
        modifier = Modifier
            .background(colors.Black),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 순환식 년 선택기 (4개 이상이므로 순환)
            GroupWheelPicker(
                modifier = Modifier.width(60.dp),
                items = years,
                selectedItem = selectedYear,
                onItemSelected = { selectedYear = it },
                displayText = { it.toString() },
                isCircular = true
            )

            // 단일 아이템 선택기 (스크롤 비활성화)
            GroupWheelPicker(
                modifier = Modifier.width(60.dp),
                items = singleItemList,
                selectedItem = selectedSingleItem,
                onItemSelected = { selectedSingleItem = it },
                displayText = { it },
                isCircular = false
            )
        }
    }
}