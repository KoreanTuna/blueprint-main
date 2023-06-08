package com.medithings.blueprint.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun dialogTitle(
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(start = 12.dp, end = 12.dp)
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        fontSize = 22.textDp,
        lineHeight = 32.textDp,
        fontWeight = FontWeight.Bold,
    )
}

data class TwoButtonDialog(
    val isShowing: Boolean,
    val title: String = "",
    val description: String = "",
    val subDescription: String = "",
    val confirmButton: String = "",
    val onClickedConfirm: (() -> Unit)? = null,
    val cancelButton: String? = null,
    val onClickedCancel: (() -> Unit)? = null
)

@Composable
fun DialogMessage(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp),
        textAlign = TextAlign.Center,
        fontSize = 17.textDp
    )
}

@Composable
fun DialogConfirmButton(
    openDialog: MutableState<Boolean>,
    onClickedConfirm: (() -> Unit)? = null,
    text: String
) {
    Box(
        modifier = Modifier
            .padding(
                start = 20.dp,
                end = 12.dp,
                bottom = 12.dp
            )
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        TextButton(
            onClick = {
                if (onClickedConfirm == null)
                    openDialog.value = false
                else
                    onClickedConfirm.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = red100
            ),
            content = {
                Text(
                    text,
                    fontSize = 13.textDp,
                    color = Color.White,
                    modifier = Modifier.wrapContentWidth(),
                    textAlign = TextAlign.Center
                )
            },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .wrapContentHeight()
                .width(98.5.dp),
        )
    }
}

@Composable
fun CommonAlertDialog(
    openDialog: MutableState<Boolean>,
    title: String,
    description: String,
    buttonText: String,
    onClickedConfirm: (() -> Unit)? = null
) {
    Column {
        VerticalMargin(dp = 32.dp)
        dialogTitle(text = title)
        DialogMessage(
            text = description
        )
        VerticalMargin(dp = 12.dp)
        DialogConfirmButton(openDialog = openDialog, onClickedConfirm, text = buttonText)
    }
}

@Preview
@Composable
fun PreviewTwoButtonAlertDialog() {
    TwoButtonAlertDialog(
        title = "타이틀",
        description = "디스크립션",
        confirmButton = "확인",
        cancelButton = null
    )
}

@Composable
fun TwoButtonAlertDialog(
    title: String,
    description: String,
    confirmButton: String,
    cancelButton: String?,
    onClickedConfirm: (() -> Unit)? = null,
    onClickedCancel: (() -> Unit)? = null
) {
    Column {
        VerticalMargin(dp = 32.dp)
        dialogTitle(text = title)
        DialogMessage(
            text = description
        )
        VerticalMargin(dp = 20.dp)
        Divider()
        VerticalMargin(dp = 12.dp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)

        ) {
            if (cancelButton != null) {
                TextButton(
                    onClick = {
                        onClickedCancel?.invoke()
                    },
                ) {
                    Text(
                        text = cancelButton,
                        color = black100,
                        fontSize = 22.textDp,
                        modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentWidth()
                    )
                }
            } else {
                Column {}
            }
            PrimaryButton(
                onClick = {
                    onClickedConfirm?.invoke()
                }, text = confirmButton,
                contentsModifier = Modifier.wrapContentWidth(),
                modifier = Modifier
                    .wrapContentHeight()
            )
        }
        VerticalMargin(dp = 12.dp)
    }
}

open class JavascriptInterface

@Composable
fun CancellationFeeDialogDetailRow(label: String, charge: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.textDp,
            color = black100
        )
        Text(
            text = charge,
            fontSize = 13.textDp,
            color = black100
        )
    }
}

@Composable
fun CancellationConfirmDialog(twoButtonDialog: TwoButtonDialog) {
    Column {
        VerticalMargin(dp = 32.dp)
        dialogTitle(text = twoButtonDialog.title)
        DialogMessage(
            text = twoButtonDialog.description
        )
        VerticalMargin(dp = 12.dp)
        Text(
            text = twoButtonDialog.subDescription,
            color = red100,
            fontSize = 13.textDp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        VerticalMargin(dp = 19.5.dp)

        Divider(
            color = black8Divider,
            thickness = 1.dp
        )
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (twoButtonDialog.cancelButton != null) {
                TextButton(
                    onClick = {
                        twoButtonDialog.onClickedCancel?.invoke()
                    },
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .sizeIn(minWidth = 73.dp)
                ) {
                    Text(
                        text = twoButtonDialog.cancelButton,
                        fontSize = 13.textDp,
                        color = black100,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = {
                    twoButtonDialog.onClickedConfirm?.invoke()
                }, text = twoButtonDialog.confirmButton,
                textColor = red100,
                fontSize = 13.textDp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .sizeIn(minWidth = 111.dp)
                    .wrapContentHeight()
            )
        }
    }
}