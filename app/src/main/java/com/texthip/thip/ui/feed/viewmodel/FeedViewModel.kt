package com.texthip.thip.ui.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texthip.thip.data.model.feed.response.AllFeedItem
import com.texthip.thip.data.model.feed.response.FeedMineInfoResponse
import com.texthip.thip.data.model.feed.response.MyFeedItem
import com.texthip.thip.data.model.users.response.RecentWriterList
import com.texthip.thip.data.repository.FeedRepository
import com.texthip.thip.data.repository.UserRepository
import com.texthip.thip.ui.feed.mock.FeedStateUpdateResult
import com.texthip.thip.ui.feed.usecase.ChangeFeedLikeUseCase
import com.texthip.thip.ui.feed.usecase.ChangeFeedSaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val selectedTabIndex: Int = 0,
    val allFeeds: List<AllFeedItem> = emptyList(),
    val myFeeds: List<MyFeedItem> = emptyList(),
    val recentWriters: List<RecentWriterList> = emptyList(),
    val myFeedInfo: FeedMineInfoResponse? = null,
    val recommendedFeeds: List<AllFeedItem> = emptyList(), // 추천글 목록
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false, // 탭 전환용 로딩
    val isPullToRefreshing: Boolean = false, // Pull to refresh용 로딩
    val isLoadingMore: Boolean = false,
    val isLastPageAllFeeds: Boolean = false,
    val isLastPageMyFeeds: Boolean = false,
    val error: String? = null
) {
    val canLoadMoreAllFeeds: Boolean get() = !isLoading && !isLoadingMore && !isRefreshing && !isPullToRefreshing && !isLastPageAllFeeds
    val canLoadMoreMyFeeds: Boolean get() = !isLoading && !isLoadingMore && !isRefreshing && !isPullToRefreshing && !isLastPageMyFeeds
    val currentTabFeeds: List<Any>
        get() = when (selectedTabIndex) {
            0 -> allFeeds
            1 -> myFeeds
            else -> emptyList()
        }
    val canLoadMoreCurrentTab: Boolean
        get() = when (selectedTabIndex) {
            0 -> canLoadMoreAllFeeds
            1 -> canLoadMoreMyFeeds
            else -> false
        }
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
    private val changeFeedLikeUseCase: ChangeFeedLikeUseCase,
    private val changeFeedSaveUseCase: ChangeFeedSaveUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private var allFeedsNextCursor: String? = null
    private var myFeedsNextCursor: String? = null
    private var isLoadingAllFeeds = false
    private var isLoadingMyFeeds = false

    init {
        loadAllFeeds()
        fetchRecentWriters()
        fetchMyFeedInfo()
        loadRecommendedFeeds()
        observeFeedUpdates()
    }

    private fun updateState(update: (FeedUiState) -> FeedUiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun observeFeedUpdates() {
        viewModelScope.launch {
            feedRepository.feedStateUpdateResult.collect { update ->
                val updatedAllFeeds = _uiState.value.allFeeds.map { feed ->
                    if (feed.feedId.toLong() == update.feedId) {
                        feed.copy(
                            isLiked = update.isLiked,
                            likeCount = update.likeCount,
                            isSaved = update.isSaved,
                            commentCount = update.commentCount
                        )
                    } else {
                        feed
                    }
                }

                val updatedMyFeeds = _uiState.value.myFeeds.map { feed ->
                    if (feed.feedId.toLong() == update.feedId) {
                        feed.copy(
                            isLiked = update.isLiked,
                            likeCount = update.likeCount,
                            isSaved = update.isSaved,
                            commentCount = update.commentCount
                        )
                    } else {
                        feed
                    }
                }

                _uiState.update {
                    it.copy(
                        allFeeds = updatedAllFeeds,
                        myFeeds = updatedMyFeeds
                    )
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        val isCurrentTab = _uiState.value.selectedTabIndex == index
        updateState { it.copy(selectedTabIndex = index) }

        when (index) {
            0 -> {
                if (isCurrentTab) {
                    // 같은 탭 다시 클릭 시: 전체 새로고침 + 스크롤 상단
                    refreshDataAndScrollToTop()
                } else {
                    // 다른 탭에서 이동: 기존처럼 현재 탭만 새로고침
                    refreshCurrentTab()
                }
            }

            1 -> {
                if (isCurrentTab) {
                    // 같은 탭 다시 클릭 시: 전체 새로고침 + 스크롤 상단
                    refreshDataAndScrollToTop()
                } else {
                    // 다른 탭에서 이동: 기존처럼 현재 탭만 새로고침
                    refreshCurrentTab()
                    if (_uiState.value.myFeedInfo == null) {
                        fetchMyFeedInfo()
                    }
                }
            }
        }
    }

    fun refreshDataAndScrollToTop() {
        refreshData()
        // 스크롤 상단 이동은 Screen에서 처리
    }

    fun refreshOnBottomNavReselect() {
        // 바텀 네비게이션에서 같은 탭 다시 클릭 시 (스크롤은 Screen에서 처리)
        refreshData()
    }

    private fun loadAllFeeds(isInitial: Boolean = true) {
        if (isLoadingAllFeeds && !isInitial) return
        if (_uiState.value.isLastPageAllFeeds && !isInitial) return

        viewModelScope.launch {
            try {
                isLoadingAllFeeds = true

                if (isInitial) {
                    updateState {
                        it.copy(
                            isLoading = true,
                            allFeeds = emptyList(),
                            isLastPageAllFeeds = false
                        )
                    }
                    allFeedsNextCursor = null
                } else {
                    updateState { it.copy(isLoadingMore = true) }
                }

                val cursor = if (isInitial) null else allFeedsNextCursor

                feedRepository.getAllFeeds(cursor).onSuccess { response ->
                    if (response != null) {
                        val currentList = if (isInitial) emptyList() else _uiState.value.allFeeds
                        updateState {
                            it.copy(
                                allFeeds = currentList + response.feedList,
                                error = null,
                                isLastPageAllFeeds = response.isLast
                            )
                        }
                        allFeedsNextCursor = response.nextCursor
                    } else {
                        updateState { it.copy(isLastPageAllFeeds = true) }
                    }
                }.onFailure { exception ->
                    updateState { it.copy(error = exception.message) }
                }
            } finally {
                isLoadingAllFeeds = false
                updateState { it.copy(isLoading = false, isLoadingMore = false) }
            }
        }
    }

    private fun loadMyFeeds(isInitial: Boolean = true) {
        if (isLoadingMyFeeds && !isInitial) return
        if (_uiState.value.isLastPageMyFeeds && !isInitial) return

        viewModelScope.launch {
            try {
                isLoadingMyFeeds = true

                if (isInitial) {
                    updateState {
                        it.copy(
                            isLoading = true,
                            myFeeds = emptyList(),
                            isLastPageMyFeeds = false
                        )
                    }
                    myFeedsNextCursor = null
                } else {
                    updateState { it.copy(isLoadingMore = true) }
                }

                val cursor = if (isInitial) null else myFeedsNextCursor

                feedRepository.getMyFeeds(cursor).onSuccess { response ->
                    if (response != null) {
                        val currentList = if (isInitial) emptyList() else _uiState.value.myFeeds
                        updateState {
                            it.copy(
                                myFeeds = currentList + response.feedList,
                                error = null,
                                isLastPageMyFeeds = response.isLast
                            )
                        }
                        myFeedsNextCursor = response.nextCursor
                    } else {
                        updateState { it.copy(isLastPageMyFeeds = true) }
                    }
                }.onFailure { exception ->
                    updateState { it.copy(error = exception.message) }
                }
            } finally {
                isLoadingMyFeeds = false
                updateState { it.copy(isLoading = false, isLoadingMore = false) }
            }
        }
    }

    fun refreshCurrentTab() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshing = true) }

            when (_uiState.value.selectedTabIndex) {
                0 -> refreshAllFeeds()
                1 -> refreshMyFeeds()
            }
            updateState { it.copy(isRefreshing = false) }
        }
    }

    fun pullToRefresh() {
        viewModelScope.launch {
            updateState { it.copy(isPullToRefreshing = true) }
            fetchRecentWriters()
            when (_uiState.value.selectedTabIndex) {
                0 -> refreshAllFeeds()
                1 -> refreshMyFeeds()
            }
            updateState { it.copy(isPullToRefreshing = false) }
        }
    }

    private suspend fun refreshAllFeeds() {
        allFeedsNextCursor = null

        feedRepository.getAllFeeds().onSuccess { response ->
            if (response != null) {
                allFeedsNextCursor = response.nextCursor
                updateState {
                    it.copy(
                        allFeeds = response.feedList,
                        isLastPageAllFeeds = response.isLast,
                        error = null
                    )
                }
            } else {
                updateState {
                    it.copy(
                        allFeeds = emptyList(),
                        isLastPageAllFeeds = true
                    )
                }
            }
        }.onFailure { exception ->
            updateState {
                it.copy(
                    error = exception.message
                )
            }
        }
    }

    private suspend fun refreshMyFeeds() {
        myFeedsNextCursor = null

        feedRepository.getMyFeeds().onSuccess { response ->
            if (response != null) {
                myFeedsNextCursor = response.nextCursor
                updateState {
                    it.copy(
                        myFeeds = response.feedList,
                        isLastPageMyFeeds = response.isLast,
                        error = null
                    )
                }
            } else {
                updateState {
                    it.copy(
                        myFeeds = emptyList(),
                        isLastPageMyFeeds = true
                    )
                }
            }
        }.onFailure { exception ->
            updateState {
                it.copy(
                    error = exception.message
                )
            }
        }
    }

    fun loadMoreFeeds() {
        if (!_uiState.value.canLoadMoreCurrentTab || _uiState.value.isRefreshing) return

        when (_uiState.value.selectedTabIndex) {
            0 -> loadAllFeeds(isInitial = false)
            1 -> loadMyFeeds(isInitial = false)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            refreshAllFeeds()
            refreshMyFeeds()
            fetchRecentWriters()
        }
    }

    fun fetchRecentWriters() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            userRepository.getMyFollowingsRecentFeeds()
                .onSuccess { data ->
                    val writers = data?.myFollowingUsers ?: emptyList()
                    updateState {
                        it.copy(
                            isLoading = false,
                            recentWriters = writers
                        )
                    }
                }
                .onFailure { exception ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    fun fetchMyFeedInfo() {
        viewModelScope.launch {
            feedRepository.getMyFeedInfo()
                .onSuccess { data ->
                    updateState {
                        it.copy(myFeedInfo = data)
                    }
                }
                .onFailure { exception ->
                    updateState {
                        it.copy(error = exception.message)
                    }
                }
        }
    }

    fun changeFeedLike(feedId: Long) {
        viewModelScope.launch {
            val currentAllFeeds = _uiState.value.allFeeds
            val currentMyFeeds = _uiState.value.myFeeds

            val allFeedToUpdate = currentAllFeeds.find { it.feedId.toLong() == feedId }
            val myFeedToUpdate = currentMyFeeds.find { it.feedId.toLong() == feedId }

            if (allFeedToUpdate == null && myFeedToUpdate == null) return@launch

            //ui 먼저 변경 ( 낙관적 업데이트 )
            val newAllFeeds = currentAllFeeds.map {
                if (it.feedId.toLong() == feedId) {
                    it.copy(
                        isLiked = !it.isLiked,
                        likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1
                    )
                } else {
                    it
                }
            }

            val newMyFeeds = currentMyFeeds.map {
                if (it.feedId.toLong() == feedId) {
                    it.copy(
                        isLiked = !it.isLiked,
                        likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1
                    )
                } else {
                    it
                }
            }

            _uiState.update { it.copy(allFeeds = newAllFeeds, myFeeds = newMyFeeds) }

            //api 호출
            val newLikeStatus = if (allFeedToUpdate != null) {
                !allFeedToUpdate.isLiked
            } else {
                !myFeedToUpdate!!.isLiked
            }

            val currentLikeCount = allFeedToUpdate?.likeCount ?: myFeedToUpdate!!.likeCount
            val currentIsSaved = allFeedToUpdate?.isSaved ?: myFeedToUpdate!!.isSaved

            changeFeedLikeUseCase(
                feedId, newLikeStatus, currentLikeCount, currentIsSaved
            )
                .onFailure {
                    _uiState.update {
                        it.copy(
                            allFeeds = currentAllFeeds,
                            myFeeds = currentMyFeeds
                        )
                    }
                }
        }
    }

    fun changeFeedSave(feedId: Long) {
        viewModelScope.launch {
            val currentAllFeeds = _uiState.value.allFeeds
            val currentMyFeeds = _uiState.value.myFeeds

            val allFeedToUpdate = currentAllFeeds.find { it.feedId.toLong() == feedId }
            val myFeedToUpdate = currentMyFeeds.find { it.feedId.toLong() == feedId }

            if (allFeedToUpdate == null && myFeedToUpdate == null) return@launch

            // (낙관적 업데이트) UI 즉시 변경
            val newAllFeeds = currentAllFeeds.map {
                if (it.feedId.toLong() == feedId) {
                    it.copy(isSaved = !it.isSaved)
                } else {
                    it
                }
            }

            val newMyFeeds = currentMyFeeds.map {
                if (it.feedId.toLong() == feedId) {
                    it.copy(isSaved = !it.isSaved)
                } else {
                    it
                }
            }

            updateState { it.copy(allFeeds = newAllFeeds, myFeeds = newMyFeeds) }

            // API 호출
            val newSaveStatus = if (allFeedToUpdate != null) {
                !allFeedToUpdate.isSaved
            } else {
                !myFeedToUpdate!!.isSaved
            }

            val currentIsLiked = allFeedToUpdate?.isLiked ?: myFeedToUpdate!!.isLiked
            val currentLikeCount = allFeedToUpdate?.likeCount ?: myFeedToUpdate!!.likeCount

            changeFeedSaveUseCase(
                feedId = feedId,
                newSaveStatus = newSaveStatus,
                currentIsLiked = currentIsLiked,
                currentLikeCount = currentLikeCount
            ).onFailure {
                _uiState.update { it.copy(allFeeds = currentAllFeeds, myFeeds = currentMyFeeds) }
            }
        }
    }

    fun updateFeedStateFromResult(result: FeedStateUpdateResult) {
        val updatedAllFeeds = _uiState.value.allFeeds.map { feed ->
            if (feed.feedId.toLong() == result.feedId) {
                feed.copy(
                    isLiked = result.isLiked,
                    likeCount = result.likeCount,
                    isSaved = result.isSaved,
                    commentCount = result.commentCount
                )
            } else {
                feed
            }
        }

        val updatedMyFeeds = _uiState.value.myFeeds.map { feed ->
            if (feed.feedId.toLong() == result.feedId) {
                feed.copy(
                    isLiked = result.isLiked,
                    likeCount = result.likeCount,
                    isSaved = result.isSaved,
                    commentCount = result.commentCount
                )
            } else {
                feed
            }
        }

        _uiState.update { it.copy(allFeeds = updatedAllFeeds, myFeeds = updatedMyFeeds) }
    }

    fun removeDeletedFeed(feedId: Long) {
        val currentAllFeeds = _uiState.value.allFeeds.filterNot { it.feedId.toLong() == feedId }
        val currentMyFeeds = _uiState.value.myFeeds.filterNot { it.feedId.toLong() == feedId }

        updateState {
            it.copy(
                allFeeds = currentAllFeeds,
                myFeeds = currentMyFeeds
            )
        }
    }

    // 추천글 로드
    private fun loadRecommendedFeeds() {
        viewModelScope.launch {
            // TODO: 실제 추천글 API 연결
            updateState { it.copy(recommendedFeeds = emptyList()) }
        }
    }
}