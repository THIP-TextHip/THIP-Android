package com.texthip.thip.ui.group.makeroom.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.R
import com.texthip.thip.ui.common.topappbar.DefaultTopAppBar
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays the group book registration screen with a top app bar and informational text.
 *
 * The screen includes a localized title bar and two centered text elements providing instructions or information
 * related to group book registration, styled according to the app theme.
 *
 * @param modifier Modifier to be applied to the root layout of the screen.
 */
@Composable
fun GroupRegisterBookScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultTopAppBar(
            title = stringResource(R.string.group_request_book),
            onLeftClick = {},
        )
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.group_thip_email),
                style = typography.smalltitle_sb600_s18_h24,
                color = colors.White
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = stringResource(R.string.group_request_book_comment),
                style = typography.copy_r400_s14,
                color = colors.White
            )
        }
    }
}

/**
 * Displays a preview of the GroupRegisterBookScreen composable with the app theme applied.
 */
@Preview
@Composable
private fun GroupRegisterBookPreview() {
    ThipTheme {
        GroupRegisterBookScreen()
    }
}
