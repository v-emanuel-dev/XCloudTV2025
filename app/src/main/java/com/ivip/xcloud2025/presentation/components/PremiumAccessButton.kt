package com.ivip.xcloudtv2025.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumAccessButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    // Cores douradas
    val goldLight = Color(0xFFFFD700)
    val goldDark = Color(0xFFB8860B)
    val goldAccent = Color(0xFFFFE55C)

    // Gradient dourado
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(goldDark, goldLight, goldAccent, goldLight, goldDark)
    )

    Card(
        onClick = {
            isPressed = !isPressed
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Aumentado de 80dp para 100dp
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = if (isPressed) 4.dp else 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = goldLight.copy(alpha = 0.3f),
                spotColor = goldLight.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = goldGradient,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = goldAccent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                onClick = {
                    isPressed = !isPressed
                    onClick()
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp) // Aumentado ainda mais para 120dp
                    .padding(horizontal = 8.dp, vertical = 8.dp) // Reduzido padding horizontal
                    .shadow(
                        elevation = if (isPressed) 4.dp else 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = goldLight.copy(alpha = 0.3f),
                        spotColor = goldLight.copy(alpha = 0.5f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = goldGradient,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = goldAccent,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        onClick = {
                            isPressed = !isPressed
                            onClick()
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(
                                horizontal = 4.dp,
                                vertical = 8.dp
                            ) // Padding mínimo nas laterais
                            .shadow(
                                elevation = if (isPressed) 4.dp else 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = goldLight.copy(alpha = 0.3f),
                                spotColor = goldLight.copy(alpha = 0.5f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = goldGradient,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = goldAccent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                            ) {
                                // Primeira linha com ícones e texto principal
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Premium",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Texto em duas linhas para evitar corte
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "ACESSO",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )

                                        Text(
                                            text = "PREMIUM",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Premium",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Subtítulo menor e mais simples
                                Text(
                                    text = "Recursos exclusivos",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp
                                    ),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }

                            // Efeito de brilho sutil
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.0f),
                                                Color.White.copy(alpha = 0.2f),
                                                Color.White.copy(alpha = 0.0f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}