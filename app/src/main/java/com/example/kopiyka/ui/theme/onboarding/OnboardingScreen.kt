package com.example.kopiyka.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopiyka.R
import com.example.kopiyka.ui.budget.BudgetDistributionScreen
import com.example.kopiyka.ui.onboarding.components.DotsIndicator
import com.example.kopiyka.ui.onboarding.models.SavingModel
import com.example.kopiyka.ui.saving.SavingInfoScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CategoryList(val categories: List<String>)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    var categories by remember { mutableStateOf(emptyList<String>()) }
    var savingModels by remember { mutableStateOf(emptyList<SavingModel>()) }
    var onboardingPhase by remember { mutableStateOf("onboarding") }

    LaunchedEffect(Unit) {
        val catJson = context.assets.open("categories.json").bufferedReader().use { it.readText() }
        val modelJson = context.assets.open("models.json").bufferedReader().use { it.readText() }

        categories = Json.decodeFromString<CategoryList>(catJson).categories
        savingModels = Json.decodeFromString<com.example.kopiyka.ui.onboarding.models.SavingModelList>(modelJson).models
    }

    val username = viewModel.username
    val budget = viewModel.budget
    val selectedModel = viewModel.savingModel
    val selectedCategories = viewModel.selectedCategories

    when (onboardingPhase) {
        "onboarding" -> {
            Scaffold(
                topBar = {
                    LinearProgressIndicator(
                        progress = { (pagerState.currentPage + 1) / 4f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DotsIndicator(pagerState.currentPage)

                        val isValid = username.isNotBlank()
                                && budget.isNotBlank()
                                && selectedModel.isNotBlank()
                                && selectedCategories.size >= 5

                        val canContinue = pagerState.currentPage < 3 || isValid
                        val buttonText = if (pagerState.currentPage < 3) "Далі" else "Продовжити"

                        Text(
                            text = buttonText,
                            color = if (canContinue)
                                colorResource(id = R.color.text_primary)
                            else
                                colorResource(id = R.color.text_secondary),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .clickable(
                                    enabled = canContinue,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    scope.launch {
                                        if (pagerState.currentPage < 3) {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        } else {
                                            viewModel.onContinue {
                                                onboardingPhase = "saving_info"
                                            }
                                        }
                                    }
                                }
                        )
                    }
                }
            ) { padding ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) { page ->
                    when (page) {
                        0 -> OnboardingNamePage(username, viewModel::onUsernameChanged)
                        1 -> OnboardingBudgetPage(budget, viewModel::onBudgetChanged)
                        2 -> OnboardingModelPage(savingModels, selectedModel, viewModel::onSavingModelSelected)
                        3 -> OnboardingCategoryPage(categories, selectedCategories, viewModel::onCategoryToggled)
                    }
                }
            }
        }

        "saving_info" -> SavingInfoScreen(
            savingModel = selectedModel,
            budget = budget.toIntOrNull() ?: 0,
            onFinished = { onboardingPhase = "budget" }
        )

        "budget" -> BudgetDistributionScreen(onStart = {
            onboardingPhase = "finished"
            onFinished()
        }
        )
    }
}
