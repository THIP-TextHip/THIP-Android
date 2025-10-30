package com.texthip.thip.ui.common.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

@Composable
fun ActionBookButton(
    bookTitle: String = stringResource(R.string.book_title),
    bookAuthor: String = stringResource(R.string.book_author),
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .background(
                color = colors.DarkGrey,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 4.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = bookTitle,
                style = typography.smalltitle_sb600_s16_h24,
                color = colors.White,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            // 저자명과 "저"를 분리
            Text(
                text = bookAuthor,
                style = typography.info_r400_s12_h24,
                color = colors.Grey,
                modifier = Modifier
                    .widthIn(max = 80.dp)
                    .padding(start = 8.dp),
                textAlign = TextAlign.Right,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                text = stringResource(R.string.author),
                style = typography.info_r400_s12_h24,
                color = colors.Grey,
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron),
                contentDescription = null,
                tint = colors.Grey,
            )
        }
    }
}

@Preview
@Composable
private fun ActionBookButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        ActionBookButton()
    }
}