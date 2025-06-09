package com.example.kopiyka.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kopiyka.R

@Composable
fun OnboardingNamePage(
    username: String,
    onUsernameChanged: (String) -> Unit
) {
    var localUsername by remember { mutableStateOf(username) }

    val error = remember(localUsername) {
        when {
            localUsername.length > 25 -> "Ім’я не повинне перевищувати 25 символів"
            localUsername.isBlank() -> "Будь ласка, введи ім’я"
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = colorResource(id = R.color.text_primary),
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Як тебе звати?",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.text_primary),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Ми хочемо до тебе звертатись по імені",
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.text_secondary),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = localUsername,
            onValueChange = {
                localUsername = it
                if (error == null || it.length <= 25) onUsernameChanged(it)
            },
            label = {
                Text(
                    text = "Мене звати...",
                    color = colorResource(id = R.color.text_primary)
                )
            },
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.text_primary),
                unfocusedBorderColor = colorResource(id = R.color.text_secondary),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = colorResource(id = R.color.text_primary),
                unfocusedLabelColor = colorResource(id = R.color.text_secondary),
                cursorColor = colorResource(id = R.color.text_primary),
                focusedTextColor = colorResource(id = R.color.text_primary),
                unfocusedTextColor = colorResource(id = R.color.text_primary)
            )
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
