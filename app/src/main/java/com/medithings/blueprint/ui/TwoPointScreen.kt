package com.medithings.blueprint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun TwoPointScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        FragmentTitle("2Point 모드 학습")
        Spacer(modifier = Modifier.height(16.dp))
        DialogSurface(
            bgColor = black12Disable
        ) {
            Text(
                lineHeight = 24.textDp,
                text = "2Point 모드 안내사항\r\n" +
                        "당신의 인공지능을 학습시켜주세요.\r\n",
                fontSize = 16.textDp,
                fontWeight = FontWeight.Normal,
                color = black65,
                modifier = Modifier.padding(
                    20.dp
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "측정 / 용량기록 / (가능하다면) 수집",
            onClick = {
                // TODO 측정
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "학습 시작",
            onClick = {
                // TODO 학습 시작
            }
        )
    }
}