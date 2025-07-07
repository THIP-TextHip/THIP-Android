package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.buttons.ActionMediumButton
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors

/**
 * Displays a centered UI with an icon, descriptive text, and an action button for requesting a book.
 *
 * @param onRequestBook Callback invoked when the action button is clicked.
 */
@Composable
fun EmptyBookSheetContent(
    onRequestBook: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_notice),
            contentDescription = null
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.group_register_book_comment),
            color = ThipTheme.colors.Grey02,
            style = ThipTheme.typography.copy_m500_s14_h20
        )
        Spacer(Modifier.height(24.dp))

        ActionMediumButton(
            text = stringResource(R.string.group_register_book),
            contentColor = colors.White,
            backgroundColor = colors.Purple,
            modifier = Modifier
                .width(97.dp)
                .height(44.dp),
            onClick = { onRequestBook() },
        )
    }
}

/**
 * Displays a preview of the EmptyBookSheetContent composable within the app's theme for design-time visualization.
 */
@Preview
@Composable
private fun EmptyBookSheetContentPreview() {
    ThipTheme {
        EmptyBookSheetContent(
            onRequestBook = {}
        )
    }
}
