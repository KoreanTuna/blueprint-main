package com.medithings.blueprint.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.medithings.blueprint.model.MacroEol
import com.medithings.blueprint.model.UARTRecord
import com.medithings.blueprint.model.UARTRecordType
import com.medithings.blueprint.pref.LocalPrefData
import com.medithings.blueprint.support.*
import com.medithings.blueprint.ui.scanner.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MLDebugScreen(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {
    val localPrefData = LocalPrefData(context = LocalContext.current)
    val scope = rememberCoroutineScope()
    val uartData = mainViewModel.uartData.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Box(modifier = Modifier.weight(1f)) {
            OutlinedCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutputSection(uartData.value?.displayMessages ?: emptyList())
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        InputSection()
    }
}

@Composable
internal fun OutputSection(records: List<UARTRecord>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "출력",
                fontSize = 22.textDp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = 20.dp,
                    start = 20.dp,
                    end = 20.dp
                )
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        val scrollState = rememberLazyListState()
        val scrollDown = remember {
            derivedStateOf { scrollState.isScrolledToTheEnd() }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = scrollState
        ) {
            if (records.isEmpty()) {
                item { Text(text = "uart_output_placeholder") }
            } else {
                records.forEach {
                    item {
                        when (it.type) {
                            UARTRecordType.INPUT -> MessageItemInput(record = it)
                            UARTRecordType.OUTPUT -> MessageItemOutput(record = it)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        LaunchedEffect(records, scrollDown.value) {
            if (!scrollDown.value || records.isEmpty()) {
                return@LaunchedEffect
            }
            launch {
                scrollState.scrollToItem(records.lastIndex)
            }
        }
    }
}

private val datFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.KOREA)

private fun UARTRecord.timeToString(): String {
    return datFormatter.format(timestamp)
}

@Composable
private fun MessageItemInput(record: UARTRecord) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = record.timeToString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = record.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
private fun MessageItemOutput(record: UARTRecord) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = record.timeToString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Text(
                text = record.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun SectionTitle(
    @DrawableRes resId: Int,
    title: String,
    menu: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        menu?.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InputSection(
    mainViewModel: MainViewModel = composableActivityViewModel(),
) {
    val text = rememberSaveable { mutableStateOf("") }
    val hint = "명렁어를 입력하세요."
    val checkedItem = rememberSaveable { mutableStateOf(MacroEol.values()[0]) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f)) {

            val scope = rememberCoroutineScope()
            val scrollState = rememberScrollState()

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 65.dp)
                    .verticalScroll(scrollState),
                value = text.value,
                label = { Text(hint) },
                onValueChange = { newValue: String ->
                    text.value = newValue
                    scope.launch {
                        scrollState.scrollTo(Int.MAX_VALUE)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                mainViewModel.sendText(text.value, checkedItem.value)
                text.value = ""
            },
            modifier = Modifier.padding(top = 6.dp),
            text = "Send"
        )
    }
}