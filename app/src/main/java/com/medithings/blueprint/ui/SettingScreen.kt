package com.medithings.blueprint.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.*
import com.medithings.blueprint.R
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SettingScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {

    val localContext = LocalContext.current
    val isAdmin by mainViewModel.adminFlow.collectAsState()

    val pwInputShow = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DialogSurface {
            Column(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                VerticalMargin(dp = 32.dp)
                dialogTitle(text = localContext.getString(R.string.setting1))
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton( // 도뇨 기준 레벨 설정
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = localContext.getString(R.string.setting2),
                    onClick = {
                        mainViewModel.navigateTo(BladderLevSetting.route)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = localContext.getString(R.string.setting3),
                    onClick = {
                        mainViewModel.navigateTo(AlarmTermSetting.route)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = localContext.getString(R.string.setting5),
                    onClick = {
                        mainViewModel.navigateTo(RegistDevice.route)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = localContext.getString(R.string.setting4),
                    onClick = {
                    }
                )
                if (isAdmin.not()) { // 관리자 모드 진입
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        color = black65,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        text = localContext.getString(R.string.setting6),
                        onClick = {
                            pwInputShow.value = true
                        }
                    )
                } else { // 관리자 모드용 기능들
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        color = black65,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        text = localContext.getString(R.string.setting7),
                        onClick = {
                            mainViewModel.navigateTo(UDS.route)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        color = black65,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        text = localContext.getString(R.string.setting8),
                        onClick = {
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        color = black65,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        text = localContext.getString(R.string.setting9),
                        onClick = {
                            mainViewModel.exitAdminMode()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (pwInputShow.value) {
            var textValue by remember { mutableStateOf(TextFieldValue("")) }

            Dialog(onDismissRequest = {}) {
                DialogSurface {
                    Column {
                        VerticalMargin(dp = 32.dp)
                        dialogTitle(text = "ADMIN MODE")
                        TextField(
                            value = textValue,
                            onValueChange = { newText -> textValue = newText },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = black65,
                                textColor = black65,
                            ),
                            placeholder = { Text("input admin password") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
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
                            Column {}
                            PrimaryButton(
                                onClick = {
                                    pwInputShow.value = false
                                    mainViewModel.setAdmin(textValue.text)
                                }, text = localContext.getString(R.string.regist11),
                                contentsModifier = Modifier.wrapContentWidth(),
                                modifier = Modifier
                                    .wrapContentHeight()
                            )
                        }
                        VerticalMargin(dp = 12.dp)
                    }
                }
            }
        }
    }
}