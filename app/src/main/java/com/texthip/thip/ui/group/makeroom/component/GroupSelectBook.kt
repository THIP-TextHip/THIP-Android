package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.OptionChipButton
import com.texthip.thip.ui.group.makeroom.mock.BookData
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays a UI component for selecting or displaying a selected book in a group creation flow.
 *
 * Shows a title and either a search prompt (when no book is selected) or the selected book's details with an option to change the selection.
 *
 * @param selectedBook The currently selected book, or null if no book is selected.
 * @param onChangeBookClick Callback invoked when the "Change" button is clicked.
 * @param onSelectBookClick Callback invoked when the search area is clicked to select a book.
 * @param modifier Modifier for styling and layout customization.
 */
@Composable
fun GroupSelectBook(
    selectedBook: BookData?,
    onChangeBookClick: () -> Unit,
    onSelectBookClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.group_select_book_title),
            style = typography.smalltitle_sb600_s18_h24,
            color = colors.White,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (selectedBook == null) {
            // 미선택 상태: 기존 검색 UI
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable { onSelectBookClick() }
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        BorderStroke(1.dp, colors.Grey02),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "검색 아이콘",
                    tint = colors.Grey01
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "검색해서 찾기",
                    style = typography.menu_m500_s16_h24,
                    color = colors.Grey
                )
            }
        } else {
            // 선택된 상태: 커버, 제목, 저자, 변경 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = painterResource(selectedBook.imageRes),
                    contentDescription = selectedBook.title,
                    modifier = Modifier
                        .height(80.dp)
                        .width(60.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = selectedBook.title,
                        color = colors.White,
                        style = typography.menu_sb600_s14_h24
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedBook.author?.let {
                        Text(
                            text = stringResource(
                                R.string.group_selected_book_author,
                                selectedBook.author
                            ),
                            color = colors.Grey01,
                            style = typography.info_r400_s12,
                            maxLines = 1
                        )
                    }
                }
                OptionChipButton(
                    text = stringResource(R.string.change),
                    onClick = onChangeBookClick,
                    isSelected = true
                )
            }
        }
    }
}

// -------------------------
// 프리뷰용 더미 BookData
// -------------------------
private val dummyBook = BookData(
    title = "호르몬 체인지",
    imageRes = R.drawable.bookcover_sample, // drawable 샘플로 교체
    author = "최정화"
)

// -------------------------
// PREVIEW: 책 미선택 상태
/**
 * Preview of the GroupSelectBook composable in the unselected state, showing the UI when no book is selected.
 */
@Preview(showBackground = true)
@Composable
fun GroupSelectBookPreview_Unselected() {
    ThipTheme {
        GroupSelectBook(
            selectedBook = null,
            onChangeBookClick = {},
            onSelectBookClick = {}
        )
    }
}

// -------------------------
// PREVIEW: 책 선택된 상태
/**
 * Preview of the GroupSelectBook composable with a selected book.
 *
 * Displays the UI as it appears when a book is selected, using dummy data for demonstration.
 */
@Preview(showBackground = true)
@Composable
fun GroupSelectBookPreview_Selected() {
    ThipTheme {
        GroupSelectBook(
            selectedBook = dummyBook,
            onChangeBookClick = {},
            onSelectBookClick = {}
        )
    }
}
