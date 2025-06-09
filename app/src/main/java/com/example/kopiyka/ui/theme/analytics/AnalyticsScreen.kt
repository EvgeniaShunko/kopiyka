package com.example.kopiyka.ui.analytics

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopiyka.ui.analytics.components.*
import java.time.LocalDate

@Composable
fun AnalyticsScreen() {
    val context = LocalContext.current
    val viewModel: AnalyticsViewModel = viewModel(factory = AnalyticsViewModelFactory(context.applicationContext as Application))

    val balances by viewModel.balances.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val chartColors = listOf(
        Color(0xFFFFC107), Color(0xFF03A9F4), Color(0xFF4CAF50),
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFFFF5722),
        Color(0xFF00BCD4), Color(0xFF8BC34A), Color(0xFFFF9800)
    )

    val slices = balances
        .filter { it.allocated - it.remaining > 0 }
        .mapIndexed { index, item ->
            PieChartSlice(
                label = item.category,
                value = (item.allocated - item.remaining).toFloat(),
                color = chartColors[index % chartColors.size]
            )
        }

    val now = LocalDate.now()
    val recentExpenses = expenses.filter {
        val date = LocalDate.parse(it.date)
        !date.isBefore(now.minusDays(6)) && !date.isAfter(now)
    }

    val dailySums = remember(recentExpenses) {
        recentExpenses.groupBy {
            LocalDate.parse(it.date).dayOfWeek.value
        }.mapValues {
            it.value.sumOf { exp -> exp.amount }.toFloat()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (expenses.isEmpty()) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "📊",
                fontSize = 64.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Тут з'являться графіки, щойно з’являться\nвитрати. Звучить чесно, правда?",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    CategoryPieChart(
                        slices = slices,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    DailyBarChart(data = dailySums)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ChartLegend(slices)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(balances) { item ->
                CategoryBalanceCard(
                    category = item.category,
                    remaining = item.remaining,
                    allocated = item.allocated
                )
            }
        }
    }
}
