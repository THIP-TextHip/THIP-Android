package com.texthip.thip.ui.common.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography


/**
 * Displays a horizontal card with a book cover image and title, styled for search results.
 *
 * The card is clickable and fills the available width. If an image resource is provided, it is shown as the book cover; otherwise, a default image is used. The title is displayed next to the image.
 *
 * @param title The book title to display.
 * @param imageRes Optional image resource ID for the book cover. Defaults to a sample image if not specified.
 * @param onClick Lambda invoked when the card is clicked.
 */
@Composable
fun CardBookSearch(
    modifier: Modifier = Modifier,
    title: String,
    imageRes: Int? = R.drawable.bookcover_sample, // 기본 이미지 리소스
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이미지
        Box(
            modifier = Modifier
                .size(width = 45.dp, height = 60.dp)
        ) {
            imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 제목
        Text(
            text = title,
            style = typography.feedcopy_r400_s14_h20,
            color = colors.White,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Displays a preview of the CardBookSearch composable with sample data for UI development.
 */
@Preview
@Composable
fun CardBookSearchPreview() {

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardBookSearch(
            title = "단 한번의 삶"
        )
    }

}