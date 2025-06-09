package com.example.kopiyka.ui.settings

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopiyka.R
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase

@Composable
fun SettingsScreen(
    getOnboardingDataUseCase: GetOnboardingDataUseCase,
) {
    val textPrimary = colorResource(id = R.color.text_primary)
    val avatarTint = colorResource(id = R.color.gray_inactive)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }

    val username = remember {
        getOnboardingDataUseCase().username.ifEmpty { "Ім’я користувача" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(96.dp),
                tint = avatarTint
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = username,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        SettingsItem("Мова", "Українська", Icons.Default.Language)
        SettingsItem("Тема", "Темна", Icons.Default.DarkMode)
        SettingsItem("Сповіщення", "", Icons.Default.Notifications)
        SettingsItem("Безпека", "", Icons.Default.Security)
        SettingsItem("Про застосунок", "", Icons.Default.Info)

        SettingsItem("Почати спочатку", "", Icons.Default.RestartAlt, onClick = {
            showDialog.value = true
        })

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = "Агов, обережніше!",
                    color = colorResource(id = R.color.text_primary)
                )
            },
            text = {
                Text(
                    text = "Готовий стерти історію? Усі графіки, покупки, і навіть ту каву за 80 грн?",
                    color = colorResource(id = R.color.text_primary)
                )
            },
            containerColor = colorResource(id = R.color.nav_bar_background),
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false

                    context.deleteFile("expenses.json")
                    val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)


                }) {
                    Text("Так", color = colorResource(id = R.color.text_primary))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Ні", color = colorResource(id = R.color.text_primary))
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    val cardBg = colorResource(id = R.color.card_background)
    val textPrimary = colorResource(id = R.color.text_primary)

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "SettingsItemScale"
    )

    Surface(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        color = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value.isNotEmpty()) {
                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
