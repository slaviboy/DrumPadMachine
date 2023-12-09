package com.slaviboy.drumpadmachine.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    @StringRes hintResId: Int = R.string.search,
    text: String,
    modifier: Modifier,
    onTextChange: (text: String) -> Unit = {},
    onSearchButtonClick: () -> Unit = {}
) {
    val enabled = true
    val singleLine = true
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxSize(),
            interactionSource = interactionSource,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = 0.037.sw,
                color = Color.White
            ),
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchButtonClick()
                    keyboardController?.hide()
                }
            ),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    vertical = 0.032.dw,
                    horizontal = 0.055.dw
                ),
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    disabledTextColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF26273B),
                    unfocusedContainerColor = Color(0xFF26273B),
                    disabledContainerColor = Color(0xFF26273B),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                ),
            )
        }
        if (text.isEmpty()) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 0.055.dw),
                text = stringResource(id = hintResId),
                fontSize = 0.037.sw,
                color = Color.LightGray
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(0.1.dw)
                .offset(x = -(0.03.dw))
                .bounceClick {
                    onSearchButtonClick()
                    keyboardController?.hide()
                }
                .padding(0.026.dw),
            colorFilter = ColorFilter.tint(Color.White),
            contentScale = ContentScale.FillHeight
        )
    }
}