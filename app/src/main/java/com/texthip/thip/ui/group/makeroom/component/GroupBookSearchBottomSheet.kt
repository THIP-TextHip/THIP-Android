package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.bottomsheet.CustomBottomSheet
import com.texthip.thip.ui.common.forms.SearchBookTextField
import com.texthip.thip.ui.common.header.HeaderMenuBarTab
import com.texthip.thip.ui.group.makeroom.mock.BookData
import com.texthip.thip.ui.group.makeroom.mock.dummyGroupBooks
import com.texthip.thip.ui.group.makeroom.mock.dummySavedBooks
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.utils.rooms.advancedImePadding

@Composable
fun GroupBookSearchBottomSheet(
    onDismiss: () -> Unit,
    onBookSelect: (BookData) -> Unit,
    onRequestBook: () -> Unit,
    savedBooks: List<BookData>,
    groupBooks: List<BookData>,
    searchResults: List<BookData> = emptyList(),
    isLoading: Boolean = false,
    isSearching: Boolean = false,
    isLoadingMoreSaved: Boolean = false,
    isLoadingMoreGroup: Boolean = false,
    isLoadingMoreSearch: Boolean = false,
    hasMoreSaved: Boolean = true,
    hasMoreGroup: Boolean = true,
    hasMoreSearch: Boolean = true,
    onSearch: (String) -> Unit = {},
    onLoadMoreSaved: () -> Unit = {},
    onLoadMoreGroup: () -> Unit = {},
    onLoadMoreSearch: () -> Unit = {},
    showGroupBooksTab: Boolean = true
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = if (showGroupBooksTab) {
        listOf(
            stringResource(R.string.group_saved_book),
            stringResource(R.string.group_book)
        )
    } else {
        listOf(stringResource(R.string.group_saved_book))
    }

    var searchText by rememberSaveable { mutableStateOf("") }

    val currentBooks = if (showGroupBooksTab && selectedTab == 1) groupBooks else savedBooks

    // 검색어가 있으면 검색 결과 사용, 없으면 탭별 도서 목록 사용
    val displayBooks = if (searchText.isNotEmpty()) {
        searchResults
    } else {
        currentBooks
    }

    val showNoSearchResultsError = searchText.isNotEmpty() && displayBooks.isEmpty() && !isSearching

    CustomBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .advancedImePadding()
        ) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                SearchBookTextField(
                    hint = stringResource(R.string.group_book_search_hint),
                    text = searchText,
                    backgroundColor = ThipTheme.colors.DarkGrey02,
                    onValueChange = {
                        searchText = it
                        onSearch(it)
                    },
                    onSearch = { onSearch(searchText) },
                )
                Spacer(Modifier.height(20.dp))
            }

            if (showNoSearchResultsError) {
                EmptyBookSheetContent(onRequestBook)
            } else {
                // 검색어가 없을 때만 탭 표시
                if (searchText.isEmpty()) {
                    HeaderMenuBarTab(
                        titles = tabs,
                        selectedTabIndex = selectedTab,
                        onTabSelected = { selectedTab = it },
                        indicatorColor = ThipTheme.colors.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(20.dp))
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colors.White
                            )
                        }
                    }

                    else -> {
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(horizontal = 20.dp)
                        ) {
                            when {
                                searchText.isNotEmpty() -> {
                                    GroupBookListWithScrollbar(
                                        books = displayBooks,
                                        onBookClick = onBookSelect,
                                        isLoadingMore = isLoadingMoreSearch,
                                        hasMore = hasMoreSearch,
                                        onLoadMore = onLoadMoreSearch
                                    )
                                }

                                !showGroupBooksTab || selectedTab == 0 -> {
                                    GroupBookListWithScrollbar(
                                        books = displayBooks,
                                        onBookClick = onBookSelect,
                                        isLoadingMore = isLoadingMoreSaved,
                                        hasMore = hasMoreSaved,
                                        onLoadMore = onLoadMoreSaved
                                    )
                                }

                                else -> {
                                    GroupBookListWithScrollbar(
                                        books = displayBooks,
                                        onBookClick = onBookSelect,
                                        isLoadingMore = isLoadingMoreGroup,
                                        hasMore = hasMoreGroup,
                                        onLoadMore = onLoadMoreGroup
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBookSearchBottomSheet_HasBooks() {
    ThipTheme {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            GroupBookSearchBottomSheet(
                onDismiss = { showSheet = false },
                onBookSelect = {},
                onRequestBook = {},
                savedBooks = dummySavedBooks,
                groupBooks = dummyGroupBooks,
                isLoading = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookSearchBottomSheet_Empty() {
    ThipTheme {
        var showSheet by remember { mutableStateOf(true) }
        if (showSheet) {
            GroupBookSearchBottomSheet(
                onDismiss = { showSheet = false },
                onBookSelect = {},
                onRequestBook = {},
                savedBooks = emptyList(),
                groupBooks = emptyList(),
                isLoading = false
            )
        }
    }
}