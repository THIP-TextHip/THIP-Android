package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.cards.CardBookSearch
import com.texthip.thip.ui.common.modal.drawVerticalScrollbar
import com.texthip.thip.ui.group.makeroom.mock.BookData
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors


/**
 * Displays a vertically scrollable list of books with a custom vertical scrollbar.
 *
 * Each book is shown as a clickable card with its title and image. Selecting a book triggers the provided callback.
 *
 * @param books The list of books to display.
 * @param onBookClick Callback invoked when a book is selected.
 */
@Composable
fun GroupBookListWithScrollbar(
    books: List<BookData>,
    onBookClick: (BookData) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .drawVerticalScrollbar(scrollState)
                .fillMaxWidth()
        ) {
            books.forEach { book ->
                CardBookSearch(
                    title = book.title,
                    imageRes = book.imageRes,
                    onClick = { onBookClick(book) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colors.Grey02)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Displays a preview of the GroupBookListWithScrollbar composable with sample book data.
 *
 * Renders the book list inside the app theme using 20 sample books for UI inspection in design tools.
 */
@Preview()
@Composable
fun PreviewBookListWithScrollbar() {
    ThipTheme {
        Column {
            GroupBookListWithScrollbar(
                books = List(20) { BookData("Book $it", R.drawable.bookcover_sample) },
                onBookClick = {}
            )
        }
    }
}

