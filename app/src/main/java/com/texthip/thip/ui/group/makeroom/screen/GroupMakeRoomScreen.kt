package com.texthip.thip.ui.group.makeroom.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.GenreChipRow
import com.texthip.thip.ui.common.buttons.ToggleSwitchButton
import com.texthip.thip.ui.common.forms.WarningTextField
import com.texthip.thip.ui.common.topappbar.InputTopAppBar
import com.texthip.thip.ui.group.makeroom.component.GroupSelectBook
import com.texthip.thip.ui.group.makeroom.component.GroupBookSearchBottomSheet
import com.texthip.thip.ui.group.makeroom.component.GroupInputField
import com.texthip.thip.ui.group.makeroom.component.GroupRoomDurationPicker
import com.texthip.thip.ui.group.makeroom.component.MemberLimitPicker
import com.texthip.thip.ui.group.makeroom.mock.BookData
import com.texthip.thip.ui.group.makeroom.mock.dummySavedBooks
import com.texthip.thip.ui.group.makeroom.mock.dummyGroupBooks
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Displays the screen for creating a new group room, allowing users to select a book, genre, set room details, meeting dates, member limit, privacy settings, and password.
 *
 * The screen validates all required fields and enables the completion button only when inputs are valid. A bottom sheet is shown for book selection, and the UI adapts based on privacy settings.
 */
@Composable
fun GroupMakeRoomScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    var selectedBook by remember { mutableStateOf<BookData?>(null) }
    var showBookSearchSheet by remember { mutableStateOf(false) }
    val genres = listOf("문학", "과학·IT", "사회과학", "인문학", "예술")
    var selectedGenreIndex by remember { mutableIntStateOf(-1) }
    var roomTitle by remember { mutableStateOf("") }
    var roomDescription by remember { mutableStateOf("") }
    var meetingStartDate by remember { mutableStateOf(LocalDate.now()) }
    var meetingEndDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var selectedCount by remember { mutableStateOf(30) }
    var isPrivate by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    val daysBetween = ChronoUnit.DAYS.between(meetingStartDate, meetingEndDate)
    val isDurationValid = (daysBetween in 1..90)
    val isCountValid = selectedCount in 2..30
    val isPasswordValid = !isPrivate || (password.length == 4)

    val isButtonEnabled = selectedBook != null &&
            selectedGenreIndex >= 0 &&
            roomTitle.isNotBlank() &&
            roomDescription.isNotBlank() &&
            isDurationValid &&
            isCountValid &&
            isPasswordValid

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(if (showBookSearchSheet) Modifier.blur(5.dp) else Modifier),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputTopAppBar(
                title = stringResource(R.string.group_making_group),
                isRightButtonEnabled = isButtonEnabled,
                onLeftClick = {},
                onRightClick = {
                    // 완료 버튼 클릭 로직
                }
            )

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Spacer(modifier = Modifier.padding(top = 20.dp))

                GroupSelectBook(
                    selectedBook = selectedBook,
                    onChangeBookClick = { showBookSearchSheet = true },
                    onSelectBookClick = { showBookSearchSheet = true }
                )

                Spacer(modifier = Modifier.padding(top = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                Text(
                    text = stringResource(R.string.group_book_genre),
                    style = typography.smalltitle_sb600_s18_h24,
                    color = colors.White,
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                GenreChipRow(
                    modifier = Modifier.width(18.dp),
                    genres = genres,
                    selectedIndex = selectedGenreIndex,
                    onSelect = { selectedGenreIndex = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (selectedGenreIndex >= 0) stringResource(R.string.group_genre_selected_comment)
                        else stringResource(R.string.group_genre_select_comment),
                        style = typography.info_r400_s12,
                        color = colors.NeonGreen
                    )
                }

                Spacer(modifier = Modifier.padding(top = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                GroupInputField(
                    title = stringResource(R.string.group_room_title),
                    hint = stringResource(R.string.group_room_title_hint),
                    value = roomTitle,
                    maxLength = 15,
                    onValueChange = { roomTitle = it }
                )

                Spacer(modifier = Modifier.padding(top = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                GroupInputField(
                    title = stringResource(R.string.group_room_explain),
                    hint = stringResource(R.string.group_room_explain_hint),
                    value = roomDescription,
                    onValueChange = { roomDescription = it }
                )

                Spacer(modifier = Modifier.padding(top = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                GroupRoomDurationPicker(
                    onDateRangeSelected = { startDate, endDate ->
                        meetingStartDate = startDate
                        meetingEndDate = endDate
                    }
                )

                Spacer(modifier = Modifier.padding(bottom = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                MemberLimitPicker(
                    selectedCount = selectedCount,
                    onCountSelected = { selectedCount = it }
                )

                Spacer(modifier = Modifier.padding(bottom = 32.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.DarkGrey02)
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))

                // --- 공개 설정 ---
                Text(
                    text = "공개 설정",
                    style = typography.smalltitle_sb600_s18_h24,
                    color = colors.White
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "비공개로 설정하기",
                        style = typography.menu_r400_s14_h24,
                        color = colors.White
                    )
                    ToggleSwitchButton(
                        isChecked = isPrivate,
                        onToggleChange = {
                            isPrivate = it
                            if (!it) password = ""
                        }
                    )
                }

                if (isPrivate) {
                    Spacer(modifier = Modifier.height(12.dp))
                    WarningTextField(
                        value = password,
                        onValueChange = { password = it },
                        hint = stringResource(R.string.group_password_hint),
                        showWarning = password.isNotEmpty() && password.length < 4,
                        warningMessage = "4자리 숫자를 입력해주세요.",
                        maxLength = 4,
                        isNumberOnly = true,
                        keyboardType = KeyboardType.NumberPassword
                    )
                }

                Spacer(modifier = Modifier.padding(top = 134.dp))
            }
        }

        if (showBookSearchSheet) {
            GroupBookSearchBottomSheet(
                onDismiss = { showBookSearchSheet = false },
                onBookSelect = { book: BookData ->
                    selectedBook = book
                    showBookSearchSheet = false
                },
                onRequestBook = {
                    showBookSearchSheet = false
                },
                savedBooks = dummySavedBooks,
                groupBooks = dummyGroupBooks,
                defaultTab = 0
            )
        }
    }
}

@Preview
@Composable
private fun GroupMakeRoomScreenPreview() {
    ThipTheme {
        GroupMakeRoomScreen()
    }
}
