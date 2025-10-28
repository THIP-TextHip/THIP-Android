package com.texthip.thip.ui.group.makeroom.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.texthip.thip.R
import com.texthip.thip.data.manager.Genre
import com.texthip.thip.ui.common.buttons.GenreChipRow
import com.texthip.thip.ui.common.buttons.ToggleSwitchButton
import com.texthip.thip.ui.common.forms.WarningTextField
import com.texthip.thip.ui.common.modal.DialogPopup
import com.texthip.thip.ui.common.topappbar.InputTopAppBar
import com.texthip.thip.ui.group.makeroom.component.GroupBookSearchBottomSheet
import com.texthip.thip.ui.group.makeroom.component.GroupInputField
import com.texthip.thip.ui.group.makeroom.component.GroupMemberLimitPicker
import com.texthip.thip.ui.group.makeroom.component.GroupRoomDurationPicker
import com.texthip.thip.ui.group.makeroom.component.GroupSelectBook
import com.texthip.thip.ui.group.makeroom.component.SectionDivider
import com.texthip.thip.ui.group.makeroom.mock.BookData
import com.texthip.thip.ui.group.makeroom.viewmodel.GroupMakeRoomUiState
import com.texthip.thip.ui.group.makeroom.viewmodel.GroupMakeRoomViewModel
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography
import com.texthip.thip.utils.rooms.advancedImePadding
import com.texthip.thip.utils.rooms.toDisplayStrings
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Composable
fun GroupMakeRoomScreen(
    onNavigateBack: () -> Unit,
    onGroupCreated: (Int) -> Unit, // roomId 전달
    modifier: Modifier = Modifier,
    viewModel: GroupMakeRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 에러 메시지 표시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            viewModel.clearError()
        }
    }

    GroupMakeRoomContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCreateGroup = {
            viewModel.createGroup(
                onSuccess = { roomId ->
                    onGroupCreated(roomId)
                },
                onError = { }
            )
        },
        onToggleConfirmDialog = viewModel::toggleConfirmDialog,
        onSelectBook = viewModel::selectBook,
        onToggleBookSearchSheet = viewModel::toggleBookSearchSheet,
        onSelectGenre = viewModel::selectGenre,
        onUpdateRoomTitle = viewModel::updateRoomTitle,
        onUpdateRoomDescription = viewModel::updateRoomDescription,
        onSetDateRange = viewModel::setDateRange,
        onSetMemberLimit = viewModel::setMemberLimit,
        onTogglePrivate = viewModel::togglePrivate,
        onUpdatePassword = viewModel::updatePassword,
        onSearchBooks = viewModel::searchBooks,
        onLoadMoreSavedBooks = viewModel::loadMoreSavedBooks,
        onLoadMoreSearchResults = viewModel::loadMoreSearchResults,
        modifier = modifier
    )
}

@Composable
fun GroupMakeRoomContent(
    modifier: Modifier = Modifier,
    uiState: GroupMakeRoomUiState,
    onNavigateBack: () -> Unit = {},
    onCreateGroup: () -> Unit = {},
    onToggleConfirmDialog: (Boolean) -> Unit = {},
    onSelectBook: (BookData) -> Unit = {},
    onToggleBookSearchSheet: (Boolean) -> Unit = {},
    onSelectGenre: (Int) -> Unit = {},
    onUpdateRoomTitle: (String) -> Unit = {},
    onUpdateRoomDescription: (String) -> Unit = {},
    onSetDateRange: (java.time.LocalDate, java.time.LocalDate) -> Unit = { _, _ -> },
    onSetMemberLimit: (Int) -> Unit = {},
    onTogglePrivate: (Boolean) -> Unit = {},
    onUpdatePassword: (String) -> Unit = {},
    onSearchBooks: (String) -> Unit = {},
    onLoadMoreSavedBooks: () -> Unit = {},
    onLoadMoreSearchResults: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.advancedImePadding()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (uiState.showBookSearchSheet || uiState.showConfirmDialog || uiState.isLoading) Modifier.blur(
                        5.dp
                    ) else Modifier
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputTopAppBar(
                title = stringResource(R.string.group_making_group),
                isRightButtonEnabled = uiState.isFormValid && !uiState.isLoading,
                onLeftClick = onNavigateBack,
                onRightClick = { onToggleConfirmDialog(true) }
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
                    selectedBook = uiState.selectedBook,
                    onChangeBookClick = { onToggleBookSearchSheet(true) },
                    onSelectBookClick = { onToggleBookSearchSheet(true) },
                    isBookPreselected = uiState.isBookPreselected
                )

                SectionDivider()

                Text(
                    text = stringResource(R.string.group_book_genre),
                    style = typography.smalltitle_sb600_s18_h24,
                    color = colors.White,
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                GenreChipRow(
                    modifier = Modifier.width(12.dp),
                    genres = uiState.genres.toDisplayStrings(),
                    selectedIndex = uiState.selectedGenreIndex,
                    onSelect = onSelectGenre,
                    horizontalArrangement = Arrangement.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(R.string.group_genre_select_comment),
                        style = typography.info_r400_s12,
                        color = colors.NeonGreen
                    )
                }

                SectionDivider()

                GroupInputField(
                    title = stringResource(R.string.group_room_title),
                    hint = stringResource(R.string.group_room_title_hint),
                    value = uiState.roomTitle,
                    maxLength = 15,
                    onValueChange = onUpdateRoomTitle
                )

                SectionDivider()

                GroupInputField(
                    title = stringResource(R.string.group_room_explain),
                    hint = stringResource(R.string.group_room_explain_hint),
                    value = uiState.roomDescription,
                    onValueChange = onUpdateRoomDescription
                )

                SectionDivider()

                GroupRoomDurationPicker(
                    onDateRangeSelected = onSetDateRange
                )

                SectionDivider()

                GroupMemberLimitPicker(
                    selectedCount = uiState.memberLimit,
                    onCountSelected = onSetMemberLimit
                )

                SectionDivider()

                Text(
                    text = stringResource(R.string.group_private_option),
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
                        text = stringResource(R.string.group_private_comment),
                        style = typography.menu_r400_s14_h24,
                        color = colors.White
                    )
                    ToggleSwitchButton(
                        isChecked = uiState.isPrivate,
                        onToggleChange = onTogglePrivate
                    )
                }

                if (uiState.isPrivate) {
                    Spacer(modifier = Modifier.height(12.dp))
                    WarningTextField(
                        value = uiState.password,
                        onValueChange = onUpdatePassword,
                        hint = stringResource(R.string.group_password_hint),
                        showWarning = uiState.password.isNotEmpty() && uiState.password.length < 4,
                        warningMessage = stringResource(R.string.group_private_warning_message),
                        maxLength = 4,
                        isNumberOnly = true,
                        keyboardType = KeyboardType.NumberPassword,
                        showIcon = true,
                        showLimit = false,
                        containerColor = colors.DarkGrey02
                    )
                }

                Spacer(modifier = Modifier.padding(top = 134.dp))
            }
        }

        if (uiState.showBookSearchSheet) {
            GroupBookSearchBottomSheet(
                onDismiss = { onToggleBookSearchSheet(false) },
                onBookSelect = { book: BookData ->
                    onSelectBook(book)
                    onToggleBookSearchSheet(false)
                },
                onRequestBook = {
                    onToggleBookSearchSheet(false)
                },
                savedBooks = uiState.savedBooks,
                groupBooks = emptyList(),
                searchResults = uiState.searchResults,
                isLoading = uiState.isLoadingBooks,
                isSearching = uiState.isSearching,
                isLoadingMoreSaved = uiState.isLoadingMoreSavedBooks,
                isLoadingMoreGroup = false,
                isLoadingMoreSearch = uiState.isLoadingMoreSearchResults,
                hasMoreSaved = !uiState.isLastSavedBooks,
                hasMoreGroup = false,
                hasMoreSearch = !uiState.isLastSearchPage,
                onSearch = onSearchBooks,
                onLoadMoreSaved = onLoadMoreSavedBooks,
                onLoadMoreGroup = {},
                onLoadMoreSearch = onLoadMoreSearchResults,
                showGroupBooksTab = false
            )
        }

        if (uiState.showConfirmDialog) {
            Dialog(
                onDismissRequest = { onToggleConfirmDialog(false) }
            ) {
                DialogPopup(
                    title = stringResource(R.string.group_create_confirm_title),
                    description = stringResource(
                        R.string.group_create_confirm_message,
                        java.time.LocalDate.now().format(DATE_FORMATTER),
                        uiState.meetingStartDate.format(DATE_FORMATTER),
                        uiState.meetingStartDate.format(DATE_FORMATTER),
                        uiState.meetingEndDate.format(DATE_FORMATTER)
                    ),
                    confirmText = stringResource(R.string.confirm),
                    cancelText = stringResource(R.string.cancel),
                    onConfirm = {
                        onToggleConfirmDialog(false)
                        onCreateGroup()
                    },
                    onCancel = {
                        onToggleConfirmDialog(false)
                    }
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.NeonGreen)
            }
        }
    }
}


@Preview
@Composable
private fun GroupMakeRoomScreenPreview() {
    ThipTheme {
        GroupMakeRoomContent(
            uiState = GroupMakeRoomUiState(
                selectedBook = BookData(
                    title = "미드나이트 라이브러리",
                    imageUrl = "https://picsum.photos/300/400?1",
                    author = "매트 헤이그",
                    isbn = "9788937477263"
                ),
                selectedGenreIndex = 2,
                roomTitle = "인생에 대해 고민하는 독서모임",
                roomDescription = "매트 헤이그의 미드나이트 라이브러리를 함께 읽으며 인생의 가능성과 선택에 대해 이야기해요. 따뜻한 마음으로 서로의 이야기를 들어주실 분들과 함께하고 싶어요.",
                memberLimit = 12,
                isPrivate = true,
                password = "1234",
                genres = Genre.entries.toList(),
                savedBooks = listOf(
                    BookData(
                        title = "코스모스",
                        imageUrl = "https://picsum.photos/300/400?2",
                        author = "칼 세이건",
                        isbn = "9788983711892"
                    ),
                    BookData(
                        title = "사피엔스",
                        imageUrl = "https://picsum.photos/300/400?3",
                        author = "유발 하라리",
                        isbn = "9788934972464"
                    )
                )
            )
        )
    }
}
