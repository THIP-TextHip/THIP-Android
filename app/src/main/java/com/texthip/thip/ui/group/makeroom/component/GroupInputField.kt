package com.texthip.thip.ui.group.makeroom.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.texthip.thip.ui.theme.ThipTheme
import com.texthip.thip.ui.theme.ThipTheme.colors
import com.texthip.thip.ui.theme.ThipTheme.typography

/**
 * Displays a labeled text input field with a character limit and live character count.
 *
 * Shows a title above the input, a hint when the field is empty, and restricts input to a maximum length.
 * The character count is displayed below the input and changes color when the limit is reached.
 *
 * @param title The label displayed above the input field.
 * @param hint The placeholder text shown when the input is empty.
 * @param value The current text value of the input field.
 * @param onValueChange Callback invoked with the new value when the input changes and does not exceed the maximum length.
 * @param maxLength The maximum number of characters allowed in the input. Defaults to 75.
 */
@Composable
fun GroupInputField(
    title: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int = 75
) {
    val isOverflow = value.length >= maxLength

    Column(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = typography.smalltitle_sb600_s18_h24,
            color = colors.White
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = value,
                onValueChange = { new ->
                    if (new.length <= maxLength) onValueChange(new)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp),
                textStyle = typography.menu_r400_s14_h24.copy(color = colors.White),
                cursorBrush = SolidColor(colors.NeonGreen),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                hint,
                                style = typography.menu_r400_s14_h24,
                                color = colors.Grey02
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${value.length} / $maxLength",
                style = typography.info_r400_s12,
                color = if (isOverflow) colors.Red else colors.NeonGreen
            )
        }
    }
}


/**
 * Displays a preview of the GroupInputField composable with a sample configuration for room title input.
 *
 * Shows the input field with a title, hint, and a 15-character limit inside the app's theme for design inspection.
 */
@Preview()
@Composable
fun PreviewRoomTitleInputField() {
    var text by remember { mutableStateOf("") }

    ThipTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            GroupInputField(
                title = "방 제목",
                hint = "방 제목을 입력해주세요",
                value = text,
                onValueChange = { text = it },
                maxLength = 15
            )
        }
    }
}
