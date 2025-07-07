package com.texthip.thip.ui.common.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Displays a horizontal row of selectable genre chips, centered within the available width.
 *
 * Each chip represents a genre from the provided list. The chip at `selectedIndex` is visually highlighted.
 * Selecting a chip invokes the `onSelect` callback with the index of the selected genre.
 *
 * @param modifier Modifier applied to the spacer between chips. Defaults to a width of 4.dp.
 * @param genres List of genre names to display as chips.
 * @param selectedIndex Index of the currently selected genre.
 * @param onSelect Callback invoked with the index of the selected genre when a chip is clicked.
 */
@Composable
fun GenreChipRow(
    modifier: Modifier = Modifier.width(4.dp),
    genres: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        genres.forEachIndexed { idx, genre ->
            OptionChipButton(
                text = genre,
                isFilled = true,
                isSelected = selectedIndex == idx,
                onClick = { onSelect(idx) }
            )
            if (idx < genres.size - 1) {
                Spacer(modifier = modifier)
            }
        }
    }
}

/**
 * Displays a preview of the GenreChipRow composable with sample genres and the first genre selected.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360)
@Composable
fun PreviewGenreChipRow() {
    GenreChipRow(
        genres = listOf("문학", "과학·IT", "사회과학", "인문학", "예술"),
        selectedIndex = 0,
        onSelect = {}
    )
}
