package com.jht.pointy.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jht.pointy.state.LoginState
import com.jht.pointy.ui.viewModel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState  by viewModel.uiState.collectAsState()
    val cs       = MaterialTheme.colorScheme

    LaunchedEffect(uiState) {
        if (uiState is LoginState.Success) onLoginSuccess()
    }

    val enter = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        enter.animateTo(0f, tween(600, easing = EaseOutCubic))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .offset(y = enter.value.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(text = "🎯", fontSize = 32.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Bonjour.",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = cs.onBackground,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Connectez-vous.",
                fontSize = 38.sp,
                fontWeight = FontWeight.Light,
                color = cs.onSurfaceVariant,
                letterSpacing = (-1).sp
            )

            Spacer(Modifier.height(52.dp))

            CleanTextField(
                value = email,
                onValueChange = { email = it },
                label = "Adresse email",
                icon = {
                    Icon(
                        Icons.Default.Email,
                        null,
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = uiState !is LoginState.Loading
            )

            Spacer(Modifier.height(16.dp))

            CleanTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mot de passe",
                icon = {
                    Icon(
                        Icons.Default.Lock,
                        null,
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                enabled = uiState !is LoginState.Loading
            )

            AnimatedVisibility(
                visible = uiState is LoginState.Error,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                if (uiState is LoginState.Error) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(4.dp)
                                .background(cs.error, RoundedCornerShape(50))
                        )
                        Text(
                            text = (uiState as LoginState.Error).message,
                            color = cs.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            val isEnabled = uiState !is LoginState.Loading
                    && email.isNotBlank()
                    && password.isNotBlank()

            Button(
                onClick = { viewModel.login(email, password) },
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = cs.onBackground,
                    disabledContainerColor = cs.outline
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                AnimatedContent(
                    targetState = uiState is LoginState.Loading,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "btnContent"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = cs.background,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Continuer →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isEnabled) cs.background else cs.onSurfaceVariant,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Accès réservé aux membres",
                    fontSize = 11.sp,
                    color = cs.outline,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}


@Composable
private fun CleanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true
) {
    val cs = MaterialTheme.colorScheme
    val isFocused = remember { mutableStateOf(false) }
    val lineColor by animateColorAsState(
        targetValue = if (isFocused.value) cs.primary else cs.outline,
        animationSpec = tween(200),
        label = "lineColor"
    )

    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        leadingIcon   = icon,
        modifier      = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color       = lineColor,
                    start       = Offset(0f, size.height),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )
            },
        singleLine           = true,
        enabled              = enabled,
        visualTransformation = visualTransformation,
        keyboardOptions      = keyboardOptions,
        shape                = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        colors               = OutlinedTextFieldDefaults.colors(
            focusedTextColor          = cs.onSurface,
            unfocusedTextColor        = cs.onSurface,
            disabledTextColor         = cs.onSurfaceVariant,
            focusedBorderColor        = Color.Transparent,
            unfocusedBorderColor      = Color.Transparent,
            disabledBorderColor       = Color.Transparent,
            focusedLabelColor         = cs.primary,
            unfocusedLabelColor       = cs.onSurfaceVariant,
            cursorColor               = cs.primary,
            focusedLeadingIconColor   = cs.primary,
            unfocusedLeadingIconColor = cs.onSurfaceVariant,
            focusedContainerColor     = cs.surface,
            unfocusedContainerColor   = cs.surface,
            disabledContainerColor    = cs.background
        )
    )
}