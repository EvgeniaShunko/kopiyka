package com.example.kopiyka.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopiyka.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun OnboardingCategoryPage(
    categories: List<String>,
    selectedCategories: List<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
 {
        Text(
            text = "Обери категорії витрат",
            fontSize = 22.sp,
            color = colorResource(id = R.color.text_primary),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { cat ->
                val isSelected = cat in selectedCategories
                val scale by animateFloatAsState(if (isSelected) 1.1f else 1f)

                Text(
                    text = cat,
                    fontSize = 18.sp,
                    color = if (isSelected)
                        colorResource(id = R.color.text_primary)
                    else
                        colorResource(id = R.color.text_secondary),
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onToggle(cat)
                        }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val selectedCount = selectedCategories.size
        if (selectedCount < 5) {
            Text(
                text = "Обери щонайменше 5 категорій ($selectedCount/5)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
