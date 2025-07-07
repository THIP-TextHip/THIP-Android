package com.texthip.thip.ui.common.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.texthip.thip.R
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays a styled search text field for book queries with a hint and search action.
 *
 * The field maintains its own input state, displays a placeholder hint, and provides clear and search icons.
 * Clicking the clear icon resets the input, while the search icon invokes the provided callback with the current text.
 *
 * @param hint The placeholder text shown when the field is empty.
 * @param onSearch Callback invoked with the current input text when the search icon is clicked.
 */
@Composable
fun SearchBookTextField(
    modifier: Modifier = Modifier,
    hint: String,
    onSearch: (String) -> Unit = {}
) {
    var text by rememberSaveable { mutableStateOf("") }
    val myStyle = typography.menu_r400_s14_h24.copy(lineHeight = 14.sp)

    Box(
        modifier = modifier.height(48.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
            },
            placeholder = {
                Text(
                    text = hint,
                    color = colors.Grey02,
                    style = myStyle
                )
            },
            textStyle = myStyle,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = colors.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = colors.DarkGrey02,
                unfocusedContainerColor = colors.DarkGrey02,
                cursorColor = colors.NeonGreen
            ),
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x_circle_grey),
                        contentDescription = "Clear text",
                        modifier = Modifier
                            .clickable { text = "" },
                        tint = Color.Unspecified
                    )

                    Spacer(Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        modifier = Modifier
                            .clickable { onSearch(text) },
                        tint = colors.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
            },
            singleLine = true
        )
    }
}

/**
 * Displays a preview of the SearchBookTextField composable with a sample hint for IDE visualization.
 */
@Preview()
@Composable
private fun SearchBookTextFieldPreview() {
    SearchBookTextField(
        hint = "책 제목, 저자검색",
        onSearch = { /* 검색 실행 */ }
    )
}
