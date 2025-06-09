package com.example.kopiyka.ui.allexpenses

import android.app.Application
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase
import com.example.kopiyka.ui.allexpenses.components.AllExpenseItemCard
import com.example.kopiyka.ui.components.EmptyStatePlaceholder
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AllExpensesScreen(
    getOnboardingDataUseCase: GetOnboardingDataUseCase
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: AllExpensesViewModel = viewModel(
        factory = AllExpensesViewModelFactory(application, getOnboardingDataUseCase)
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadExpenses()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filters by viewModel.availableFilters.collectAsState()
    val groupedExpenses by viewModel.groupedExpenses.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()

    val extended = remember(filters) { List(999) { filters[it % filters.size] } }
    val centerIndex = extended.size / 2
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(centerIndex)
    }

    val centeredItemIndex by remember {
        derivedStateOf { getCenteredItemIndex(listState) }
    }

    val activeFilter = extended.getOrNull(centeredItemIndex) ?: filters.first()

    LaunchedEffect(activeFilter) {
        viewModel.setFilter(activeFilter)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(horizontal = 64.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            itemsIndexed(extended) { index, filter ->
                val isSelected = index == centeredItemIndex

                val animatedFontSize by animateFloatAsState(
                    targetValue = if (isSelected) 20f else 16f,
                    label = "FontSize"
                )

                val animatedFontWeight by animateFloatAsState(
                    targetValue = if (isSelected) 700f else 400f,
                    label = "FontWeight"
                )

                Text(
                    text = filter,
                    fontSize = animatedFontSize.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight(animatedFontWeight.toInt()),
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeFilter == "Дата") {
                if (groupedExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyStatePlaceholder(
                                message = "Тут можуть бути ваші витрати",
                                emoji = "💰"
                            )
                        }
                    }
                } else {
                    groupedExpenses.forEach { (date, expenses) ->
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = date, fontSize = 16.sp)
                            }
                        }
                        items(expenses) { expense ->
                            AllExpenseItemCard(item = expense, showCategory = true)
                        }
                    }
                }
            } else {
                if (filteredExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyStatePlaceholder(
                                message = "Тут можуть бути ваші витрати",
                                emoji = "💰"
                            )
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        AllExpenseItemCard(item = expense, showCategory = false)
                    }
                }
            }
        }
    }
}

fun getCenteredItemIndex(listState: LazyListState): Int {
    val layoutInfo = listState.layoutInfo
    val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
    var closestItemIndex = listState.firstVisibleItemIndex
    var smallestDistance = Int.MAX_VALUE
    layoutInfo.visibleItemsInfo.forEach { item ->
        val itemCenter = item.offset + item.size / 2
        val distance = abs(itemCenter - viewportCenter)
        if (distance < smallestDistance) {
            smallestDistance = distance
            closestItemIndex = item.index
        }
    }
    return closestItemIndex
}