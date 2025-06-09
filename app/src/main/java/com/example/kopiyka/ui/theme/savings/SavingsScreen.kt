package com.example.kopiyka.ui.savings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopiyka.R
import com.example.kopiyka.data.local.OnboardingPreferencesDataSource
import com.example.kopiyka.data.repository.OnboardingRepositoryImpl
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase

@Composable
fun SavingsScreen() {
    val context = LocalContext.current
    val getOnboardingData = remember {
        GetOnboardingDataUseCase(
            OnboardingRepositoryImpl(OnboardingPreferencesDataSource(context))
        )
    }

    val onboardingData = remember { getOnboardingData() }

    val goalAmount = onboardingData.budget
    val currentAmount = goalAmount - onboardingData.adjustedBudget
    val savingModel = onboardingData.savingModel

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        showDialog = true
                    }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) {
            Text(
                text = "На твою мрію відкладено",
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$currentAmount гривень",
                fontSize = 28.sp,
                color = Color.White
            )

            JarImage(currentAmount = currentAmount, goalAmount = goalAmount)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text(
                                text = "Я все зрозумів",
                                color = Color.White
                            )
                        }
                    }
                },
                containerColor = colorResource(id = R.color.nav_bar_background),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Автоматичне поповнення",
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                text = {
                    Text(
                        text = "Ця банка поповнюється автоматично щомісяця з відкладених грошей за моделлю \"$savingModel\"",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

@Composable
fun JarImage(currentAmount: Int, goalAmount: Int) {
    val percentage = currentAmount.toFloat() / goalAmount.toFloat()

    val imageRes = when {
        percentage < 0.25f -> R.drawable.empty_jar
        percentage < 0.5f -> R.drawable.almost_empty_jar
        percentage < 0.75f -> R.drawable.half_full_jar
        else -> R.drawable.full_jar
    }

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Jar Image",
        modifier = Modifier
            .width(500.dp)
            .height(750.dp)
    )
}
