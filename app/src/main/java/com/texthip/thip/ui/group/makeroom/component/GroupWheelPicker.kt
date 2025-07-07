package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Displays a vertically scrollable wheel picker for selecting an item from a list, with optional circular scrolling and animated snapping.
 *
 * The picker centers the selected item and allows users to scroll or drag to change the selection. Circular (infinite) scrolling is enabled if specified and the item count is greater than two. Scrolling is disabled if only one item is present. The currently selected item is highlighted, and a callback is invoked when the selection changes.
 *
 * @param modifier Modifier for styling and layout.
 * @param items List of items to display in the picker.
 * @param selectedItem The currently selected item.
 * @param onItemSelected Callback invoked when the selected item changes.
 * @param displayText Function to convert an item to its display string.
 * @param selectedBackgroundColor Background color for the selected item highlight.
 * @param itemHeight Height of each item in dp.
 * @param isCircular Enables circular scrolling if true and item count is greater than two.
 */
@Composable
fun <T> GroupWheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayText: (T) -> String = { it.toString() },
    selectedBackgroundColor: Color = colors.DarkGrey50,
    itemHeight: Int = 20,
    isCircular: Boolean = true
) {
    if (items.isEmpty()) return

    // 아이템이 하나인 경우 스크롤 비활성화
    val isScrollEnabled = items.size > 1
    val circular = isCircular && items.size > 2

    val selectedIndex = items.indexOf(selectedItem)
    val animatableOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    var velocity by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.dp.toPx() }
    val spacingPx = with(density) { 9.dp.toPx() }
    val itemSpacing = itemHeightPx + spacingPx

    // index가 음수/양수 모두에서 올바르게 작동하도록
    fun getCircularIndex(index: Int): Int {
        val size = items.size
        return ((index % size) + size) % size
    }

    // offset을 항상 0 ~ (items.size-1)*itemSpacing 범위로
    fun normalizeOffset(offset: Float): Float {
        if (!circular) return offset
        val total = items.size * itemSpacing
        return ((offset % total) + total) % total
    }

    // 오프셋을 아이템 인덱스로 변환 (0이 중앙)
    fun offsetToIndex(offset: Float): Int {
        val total = items.size * itemSpacing
        val normalized = if (circular) normalizeOffset(offset) else offset
        val centerIndex = (-normalized / itemSpacing).roundToInt()
        return if (circular) getCircularIndex(centerIndex) else centerIndex.coerceIn(
            0,
            items.size - 1
        )
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

    // 오프셋이 바뀔 때 마다 선택 아이템을 갱신
    LaunchedEffect(animatableOffset.value) {
        if (!isDragging && isScrollEnabled) {
            val newSelectedIndex = offsetToIndex(animatableOffset.value)
            if (items[newSelectedIndex] != selectedItem) {
                onItemSelected(items[newSelectedIndex])
            }
        }
    }

    Box(
        modifier = modifier
            .height((itemHeight * 3 + 36).dp)
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
                style = typography.info_r400_s12,
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
            val currOffset =
                if (circular && isScrollEnabled) normalizeOffset(animatableOffset.value) else animatableOffset.value
            val centerIndex = if (isScrollEnabled) (-currOffset / itemSpacing).roundToInt() else 0

            // 중앙 + 위 아래 한 개만 보이도록!
            val visibleRange = if (isScrollEnabled) -1..1 else 0..0

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
                        style = typography.info_r400_s12,
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


/**
 * Displays a preview of the GroupWheelPicker composable with two examples: a circular year picker and a single-item picker.
 *
 * Demonstrates usage of the wheel picker with both multiple and single item lists, showing circular and non-circular scrolling behavior.
 */
@Preview(showBackground = true)
@Composable
fun WheelPickerPreview() {
    var selectedYear by remember { mutableStateOf(2025) }
    val years = (2020..2030).toList() // 11개

    var selectedSingleItem by remember { mutableStateOf("Only Item") }
    val singleItemList = listOf("Only Item") // 1개

    Box(
        modifier = Modifier
            .background(Color.Black),
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