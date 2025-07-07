package com.texthip.thip.ui.common.bottomsheet

// com.texthip.thip.ui.common.bottomsheet.CustomBottomSheet.kt

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import kotlinx.coroutines.launch

/**
 * Displays a custom animated bottom sheet with drag-to-dismiss and outside-tap dismissal.
 *
 * The sheet slides up from the bottom of the screen and can be dismissed by dragging downward past a threshold or by tapping outside the sheet area. The content of the sheet is provided via a composable slot scoped to `ColumnScope`.
 *
 * @param onDismiss Called when the sheet is dismissed by user interaction.
 * @param content Composable content to display inside the bottom sheet.
 */
@Composable
fun CustomBottomSheet(
    onDismiss: () -> Unit,
    // 핵심: ColumnScope로 slot 전달!
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val animatableOffset = remember { Animatable(300f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDismissing by remember { mutableStateOf(false) }

    // 등장 애니메이션
    LaunchedEffect(Unit) {
        animatableOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    // 바깥 클릭 감지
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isDismissing) {
                    isDismissing = true
                    scope.launch {
                        animatableOffset.animateTo(300f, tween(300))
                        onDismiss()
                    }
                }
            }
            .zIndex(1f)
    )

    // BottomSheet 본체
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (offsetY + animatableOffset.value).dp)
                .background(
                    color = colors.DarkGrey,
                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)
                )
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            if (dragAmount > 0) {
                                offsetY += dragAmount / 2
                            }
                        },
                        onDragEnd = {
                            if (offsetY > 100f && !isDismissing) {
                                isDismissing = true
                                scope.launch {
                                    animatableOffset.animateTo(300f, tween(300))
                                    onDismiss()
                                }
                            } else {
                                offsetY = 0f
                            }
                        }
                    )
                }
                .clickable(enabled = true) {} // 내부 클릭 먹히게
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content() // <--- 이 부분이 핵심! slot에 원하는 Compose UI를 전달
            }
        }
    }
}


/**
 * Displays a preview of the CustomBottomSheet composable with sample content and dismissal behavior.
 *
 * Shows a main content area with a bottom sheet overlay that can be dismissed by tapping a button or outside the sheet.
 */
@Preview()
@Composable
fun PreviewCustomBottomSheet() {
    var showSheet by remember { mutableStateOf(true) }

    ThipTheme {
        Box(Modifier.fillMaxSize()) {
            // 배경 컨텐츠 예시
            Text(
                text = "Main Content Area",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
            )

            if (showSheet) {
                CustomBottomSheet(
                    onDismiss = { showSheet = false }
                ) {
                    Text(
                        "바텀 시트 예시",
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { showSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("닫기", color = Color.Black)
                    }
                }
            }
        }
    }
}
