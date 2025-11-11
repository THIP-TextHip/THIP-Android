package com.texthip.thip.ui.group.makeroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texthip.thip.R
import com.texthip.thip.data.manager.Genre
import com.texthip.thip.data.model.book.response.BookSavedResponse
import com.texthip.thip.data.model.book.response.BookSearchItem
import com.texthip.thip.data.model.rooms.request.CreateRoomRequest
import com.texthip.thip.data.provider.StringResourceProvider
import com.texthip.thip.data.repository.BookRepository
import com.texthip.thip.data.repository.RoomsRepository
import com.texthip.thip.ui.group.makeroom.mock.BookData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GroupMakeRoomViewModel @Inject constructor(
    private val roomsRepository: RoomsRepository,
    private val bookRepository: BookRepository,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupMakeRoomUiState())
    val uiState: StateFlow<GroupMakeRoomUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var loadMoreSearchJob: Job? = null
    private var savedBooksCursor: String? = null

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    }

    private fun updateState(update: (GroupMakeRoomUiState) -> GroupMakeRoomUiState) {
        _uiState.value = update(_uiState.value)
    }

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            roomsRepository.getGenres()
                .onSuccess { genresList ->
                    updateState { it.copy(genres = genresList) }
                }
        }
    }

    fun selectBook(book: BookData) {
        updateState { it.copy(selectedBook = book) }
    }

    fun setPreselectedBook(isbn: String, title: String, imageUrl: String, author: String) {
        val preselectedBook = BookData(
            title = title,
            imageUrl = imageUrl,
            author = author,
            isbn = isbn
        )
        updateState {
            it.copy(
                selectedBook = preselectedBook,
                isBookPreselected = true
            )
        }
    }

    fun toggleBookSearchSheet(show: Boolean) {
        updateState { it.copy(showBookSearchSheet = show) }
        if (show) {
            loadBooks()
        }
    }

    private fun loadBooks() {
        loadSavedBooks(isInitial = true)
        // 모임 생성 화면에서는 저장된 책만 표시
    }

    fun loadSavedBooks(isInitial: Boolean = false) {
        val currentState = _uiState.value
        if (currentState.isLoadingBooks || currentState.isLoadingMoreSavedBooks) return
        if (!isInitial && currentState.isLastSavedBooks) return

        viewModelScope.launch {
            try {
                if (isInitial) {
                    updateState {
                        it.copy(
                            savedBooks = emptyList(),
                            isLastSavedBooks = false,
                            isLoadingBooks = true
                        )
                    }
                    savedBooksCursor = null
                } else {
                    updateState { it.copy(isLoadingMoreSavedBooks = true) }
                }

                val cursor = if (isInitial) null else savedBooksCursor

                bookRepository.getBooks("SAVED", cursor)
                    .onSuccess { response ->
                        if (response != null) {
                            val currentList =
                                if (isInitial) emptyList() else _uiState.value.savedBooks
                            val newBooks = response.bookList.map { it.toBookData() }
                            updateState {
                                it.copy(
                                    savedBooks = currentList + newBooks,
                                    isLastSavedBooks = response.isLast
                                )
                            }
                            savedBooksCursor = response.nextCursor
                        } else {
                            updateState { it.copy(isLastSavedBooks = true) }
                        }
                    }
                    .onFailure { exception ->
                        if (isInitial) {
                            updateState { it.copy(savedBooks = emptyList()) }
                        }
                    }
            } finally {
                updateState {
                    it.copy(
                        isLoadingBooks = false,
                        isLoadingMoreSavedBooks = false
                    )
                }
            }
        }
    }

    fun loadMoreSavedBooks() {
        loadSavedBooks(isInitial = false)
    }

    private fun BookSavedResponse.toBookData(): BookData {
        return BookData(
            title = this.bookTitle,
            imageUrl = this.bookImageUrl,
            author = this.authorName,
            isbn = this.isbn
        )
    }

    private fun BookSearchItem.toBookData(): BookData {
        return BookData(
            title = this.title,
            imageUrl = this.imageUrl,
            author = this.authorName,
            isbn = this.isbn
        )
    }

    fun searchBooks(query: String) {
        searchJob?.cancel()
        loadMoreSearchJob?.cancel()

        if (query.isBlank()) {
            updateState {
                it.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    searchPage = 1,
                    isLastSearchPage = false,
                    currentSearchQuery = ""
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // 디바운싱
            updateState {
                it.copy(
                    isSearching = true,
                    searchResults = emptyList(),
                    searchPage = 1,
                    isLastSearchPage = false,
                    currentSearchQuery = query
                )
            }

            try {
                val result = bookRepository.searchBooks(query, page = 1, isFinalized = false)
                result.onSuccess { response ->
                    if (response != null) {
                        val searchResults = response.searchResult.map { it.toBookData() }
                        updateState {
                            it.copy(
                                searchResults = searchResults,
                                searchPage = response.page,
                                isLastSearchPage = response.last,
                                isSearching = false
                            )
                        }
                    } else {
                        updateState {
                            it.copy(
                                searchResults = emptyList(),
                                isSearching = false,
                                isLastSearchPage = true
                            )
                        }
                    }
                }.onFailure {
                    updateState {
                        it.copy(
                            searchResults = emptyList(),
                            isSearching = false
                        )
                    }
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false
                    )
                }
            }
        }
    }

    fun loadMoreSearchResults() {
        val currentState = _uiState.value
        if (currentState.isLoadingMoreSearchResults ||
            currentState.isSearching ||
            currentState.isLastSearchPage ||
            currentState.searchResults.isEmpty() ||
            currentState.currentSearchQuery.isBlank()
        ) {
            return
        }

        loadMoreSearchJob?.cancel()
        loadMoreSearchJob = viewModelScope.launch {
            updateState { it.copy(isLoadingMoreSearchResults = true) }

            try {
                val nextPage = currentState.searchPage + 1
                val result = bookRepository.searchBooks(
                    currentState.currentSearchQuery,
                    page = nextPage,
                    isFinalized = false
                )
                result.onSuccess { response ->
                    if (response != null) {
                        val newResults = response.searchResult.map { it.toBookData() }
                        updateState {
                            it.copy(
                                searchResults = currentState.searchResults + newResults,
                                searchPage = response.page,
                                isLastSearchPage = response.last,
                                isLoadingMoreSearchResults = false
                            )
                        }
                    } else {
                        updateState {
                            it.copy(
                                isLoadingMoreSearchResults = false,
                                isLastSearchPage = true
                            )
                        }
                    }
                }.onFailure {
                    updateState {
                        it.copy(isLoadingMoreSearchResults = false)
                    }
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(isLoadingMoreSearchResults = false)
                }
            }
        }
    }

    fun selectGenre(index: Int) {
        updateState { it.copy(selectedGenreIndex = index) }
    }

    fun updateRoomTitle(title: String) {
        updateState { it.copy(roomTitle = title) }
    }

    fun updateRoomDescription(description: String) {
        updateState { it.copy(roomDescription = description) }
    }

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        updateState {
            it.copy(
                meetingStartDate = startDate,
                meetingEndDate = endDate
            )
        }
    }

    fun setMemberLimit(count: Int) {
        updateState { it.copy(memberLimit = count) }
    }

    fun togglePrivate(isPrivate: Boolean) {
        updateState {
            it.copy(
                isPrivate = isPrivate,
                password = if (!isPrivate) "" else it.password
            )
        }
    }

    fun updatePassword(password: String) {
        updateState { it.copy(password = password) }
    }

    fun toggleConfirmDialog(show: Boolean = true) {
        updateState { it.copy(showConfirmDialog = show) }
    }

    fun createGroup(onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value

        if (!currentState.isFormValid) {
            onError(stringResourceProvider.getString(R.string.error_form_validation))
            return
        }

        val selectedBook = currentState.selectedBook
        if (selectedBook?.isbn == null) {
            onError(stringResourceProvider.getString(R.string.error_book_info_invalid))
            return
        }

        viewModelScope.launch {
            try {
                updateState { it.copy(isLoading = true, errorMessage = null) }

                val request = CreateRoomRequest(
                    isbn = selectedBook.isbn,
                    category = getApiCategoryName(currentState.selectedGenreIndex),
                    roomName = currentState.roomTitle.trim(),
                    description = currentState.roomDescription.trim(),
                    progressStartDate = currentState.meetingStartDate.format(DATE_FORMATTER),
                    progressEndDate = currentState.meetingEndDate.format(DATE_FORMATTER),
                    recruitCount = currentState.memberLimit,
                    password = if (currentState.isPrivate) currentState.password else null,
                    isPublic = !currentState.isPrivate
                )

                val result = roomsRepository.createRoom(request)
                result.onSuccess { roomId ->
                    onSuccess(roomId)
                }.onFailure { exception ->
                    onError(
                        stringResourceProvider.getString(
                            R.string.error_room_creation_failed,
                            exception.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                onError(
                    stringResourceProvider.getString(
                        R.string.error_network_error,
                        e.message ?: ""
                    )
                )
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun getApiCategoryName(genreIndex: Int): String {
        val currentGenres = uiState.value.genres
        if (genreIndex >= 0 && genreIndex < currentGenres.size) {
            val genre = currentGenres[genreIndex]
            return genre.networkApiCategory
        }
        return Genre.getDefault().networkApiCategory
    }

    fun clearError() {
        updateState { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        loadMoreSearchJob?.cancel()
    }
}