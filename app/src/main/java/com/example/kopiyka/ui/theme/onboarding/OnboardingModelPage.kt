package com.example.kopiyka.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kopiyka.R
import com.example.kopiyka.ui.onboarding.models.SavingModel

@Composable
fun OnboardingModelPage(
    models: List<SavingModel>,
    selectedModel: String,
    onModelSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Обери модель заощаджень",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.text_primary),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            models.forEach { model ->
                SavingOption(
                    model = model,
                    isSelected = selectedModel == model.title,
                    onClick = { onModelSelected(model.title) }
                )
            }
        }
    }
}

@Composable
fun SavingOption(
    model: SavingModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected)
        colorResource(id = R.color.text_primary)
    else
        colorResource(id = android.R.color.transparent)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(width = 2.dp, color = borderColor),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = android.R.color.transparent))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.titleLarge,
                color = colorResource(id = R.color.text_primary)
            )
            Text(
                text = model.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.text_primary)
            )
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(id = R.color.text_secondary),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
