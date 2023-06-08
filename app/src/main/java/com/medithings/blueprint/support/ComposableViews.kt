package com.medithings.blueprint.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun BaseLayout(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .padding(bottom = 40.dp)
    ) {
        content()
    }
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    height: Dp = 50.dp,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    width: Dp? = null,
    contentsModifier: Modifier? = null,
    modifier: Modifier? = null,
    isActive: Boolean = true,
    color: Color = demoPrimary,
    fontSize: TextUnit? = null
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isActive) color else black12Disable
        ),
        content = {
            Text(
                text,
                fontSize = fontSize ?: 24.textDp,
                color = Color.White,
                modifier = contentsModifier
                    ?: if (width == null) Modifier.fillMaxWidth() else Modifier.width(width),
                textAlign = TextAlign.Center
            )
        },
        shape = RoundedCornerShape(25.dp),
        modifier = modifier ?: Modifier
            .height(height)
            .padding(start = paddingStart, end = paddingEnd),
    )
}

@Composable
fun Button(
    onClick: () -> Unit,
    text: String,
    height: Dp = 50.dp,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    contentsModifier: Modifier = Modifier.wrapContentWidth(),
    modifier: Modifier? = null,
    textColor: Color = Color.Black,
    fontSize: TextUnit = 16.textDp
) {
    OutlinedButton(
        onClick = onClick,
        content = {
            Text(
                text,
                fontSize = fontSize,
                color = textColor,
                modifier = contentsModifier,
                textAlign = TextAlign.Center
            )
        },
        shape = RoundedCornerShape(25.dp),
        modifier = modifier ?: Modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = paddingStart, end = paddingEnd),
    )
}

@Composable
fun CloseButton(
    onClick: () -> Unit,
    size: Dp = 20.dp
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Default.Close,
            "Close",
            Modifier.size(size)
        )
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    size: Dp = 20.dp
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            Icons.Default.ArrowBack,
            "Back",
            Modifier.size(size)
        )

    }
}

@Composable
fun ColumnScope.VerticalMargin(dp: Dp) {
    Spacer(
        modifier = Modifier
            .height(dp)
            .fillMaxWidth()
    )
}

@Composable
fun RowScope.HorizontalMargin(dp: Dp) {
    Spacer(
        modifier = Modifier
            .wrapContentHeight()
            .width(dp)
    )
}

@Composable
fun Divider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun BoxScope.DividerInBox() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(black8Divider)
            .align(Alignment.BottomCenter)
    )
}

@Composable
fun arrangementBottom(content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        content()
    }
}


@Composable
fun FragmentTitle(text: String, modifier: Modifier? = null) {
    Text(
        text = text,
        fontSize = 26.textDp,
        fontWeight = FontWeight.Bold,
        modifier = modifier ?: Modifier.padding(
            top = 20.dp,
            start = 20.dp,
            end = 20.dp
        )
    )
}

@Composable
fun ClickableText(
    text: String,
    keyword: String,
    textColor: Color,
    fontSize: TextUnit,
    clickableTextColor: Color = textColor,
    modifier: Modifier? = null,
    textAlign: TextAlign? = TextAlign.Left,
    onClick: (Int) -> Unit
) {
    val startIndex = text.indexOf(keyword)
    val endIndex = startIndex + keyword.length
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            SpanStyle(
                color = textColor,
                fontSize = fontSize,
            ), 0, text.length
        )
        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = clickableTextColor
            ),
            start = startIndex,
            end = endIndex
        )
        addStringAnnotation("clicked", "onClicked", startIndex, endIndex)
    }
    ClickableText(
        text = annotatedString,
        modifier = modifier ?: Modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations("clicked", offset, offset).firstOrNull()?.let {
                onClick(offset)
            }
        })
}

@Composable
fun ColoredText(
    text: String,
    keyword: String,
    textColor: Color,
    accentColor: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight? = null,
    modifier: Modifier? = null
) {
    val startIndex = text.indexOf(keyword)
    val endIndex = startIndex + keyword.length
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            SpanStyle(
                color = textColor,
                fontSize = fontSize
            ), 0, text.length
        )
        addStyle(
            style = SpanStyle(
                color = accentColor
            ),
            start = startIndex,
            end = endIndex
        )
        addStringAnnotation("colored", "colored", startIndex, endIndex)
    }

    Text(
        text = annotatedString,
        fontWeight = fontWeight ?: FontWeight.Normal,
        modifier = modifier ?: Modifier
    )
}

@Composable
fun StyledText(
    text: String,
    keyword: String,
    textColor: Color,
    fontSize: TextUnit,
    style: SpanStyle,
    modifier: Modifier? = null
) {
    val startIndex = text.indexOf(keyword)
    val endIndex = startIndex + keyword.length
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            SpanStyle(
                color = textColor,
                fontSize = fontSize
            ), 0, text.length
        )
        addStyle(
            style = style,
            start = startIndex,
            end = endIndex
        )
        addStringAnnotation("styled", "styled", startIndex, endIndex)
    }

    Text(
        text = annotatedString,
        modifier = modifier ?: Modifier
    )
}

@Composable
fun KeepDialog(
    isOpenDialog: MutableState<Boolean>,
    dialog: @Composable (MutableState<Boolean>) -> Unit
) {
    if (isOpenDialog.value) {
        Dialog(onDismissRequest = {
            isOpenDialog.value = false
        }) {
            DialogSurface {
                dialog(isOpenDialog)
            }
        }
    }
}

@Composable
fun DialogSurface(
    bgColor: Color = Color.White,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = elevation,
        color = bgColor
    ) {
        content()
    }
}