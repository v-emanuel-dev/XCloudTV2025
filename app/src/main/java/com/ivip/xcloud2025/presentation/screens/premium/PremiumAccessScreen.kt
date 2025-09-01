package com.ivip.xcloudtv2025.presentation.screens.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivip.xcloudtv2025.presentation.theme.XcloudTVTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumAccessScreen(
    onClose: () -> Unit
) {
    // Cores douradas para o tema premium
    val goldLight = Color(0xFFFFD700)
    val goldDark = Color(0xFFB8860B)
    val goldAccent = Color(0xFFFFE55C)

    // Gradient de fundo
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1421),
            Color(0xFF1A1A1A),
            Color(0xFF0D1421)
        )
    )

    XcloudTVTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Header com ícone premium
                PremiumHeader()

                Spacer(modifier = Modifier.height(32.dp))

                // Título principal
                Text(
                    text = "ACESSO PREMIUM",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        letterSpacing = 2.sp
                    ),
                    color = goldLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtítulo
                Text(
                    text = "Desbloqueie o poder completo do Xcloud TV",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Conteúdo principal (dummy text)
                PremiumContent()

                Spacer(modifier = Modifier.height(40.dp))

                // Botão de ação premium
                PremiumActionButton()

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Botão de fechar no canto superior direito
            CloseButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
            )

            // Efeitos visuais decorativos
            PremiumDecorations()
        }
    }
}

@Composable
private fun PremiumHeader() {
    val goldLight = Color(0xFFFFD700)
    val goldDark = Color(0xFFB8860B)

    Box(
        contentAlignment = Alignment.Center
    ) {
        // Círculo de fundo com gradient
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(goldLight, goldDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Premium",
                tint = Color.Black,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
private fun PremiumContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Recursos Exclusivos Premium",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val premiumFeatures = listOf(
                "🎬 Acesso a mais de 10.000 canais premium",
                "🔥 Qualidade 4K Ultra HD em todos os canais",
                "⚡ Streaming sem buffering garantido",
                "🌟 Canais esportivos exclusivos e pay-per-view",
                "🎵 Bibliotecas de música e podcasts premium",
                "📱 Sincronização multi-dispositivos",
                "🚀 Velocidade de carregamento 3x mais rápida",
                "🎯 Recomendações personalizadas por IA",
                "💎 Suporte prioritário 24/7",
                "🔒 Sem anúncios e experiência premium completa"
            )

            premiumFeatures.forEach { feature ->
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum " +
                        "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                        "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n" +
                        "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium " +
                        "doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore " +
                        "veritatis et quasi architecto beatae vitae dicta sunt explicabo.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Justify,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun PremiumActionButton() {
    val goldLight = Color(0xFFFFD700)
    val goldDark = Color(0xFFB8860B)
    val goldAccent = Color(0xFFFFE55C)

    Button(
        onClick = { /* Implementar ação premium */ },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(goldDark, goldLight, goldAccent, goldLight, goldDark)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "ATIVAR PREMIUM",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = Color.Black.copy(alpha = 0.6f)
            )
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Fechar",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PremiumDecorations() {
    // Decorações visuais de fundo (estrelas, etc.)
    // Pode ser expandido com animações futuramente
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Estrelas decorativas nos cantos - usando valores fixos para evitar problemas de compilação
        val decorativeStars = listOf(
            Triple(50.dp, 100.dp, 12.dp),
            Triple(200.dp, 80.dp, 8.dp),
            Triple(350.dp, 150.dp, 10.dp),
            Triple(100.dp, 400.dp, 14.dp),
            Triple(300.dp, 350.dp, 9.dp),
            Triple(150.dp, 500.dp, 11.dp),
            Triple(400.dp, 450.dp, 13.dp),
            Triple(250.dp, 250.dp, 8.dp)
        )

        decorativeStars.forEachIndexed { index, (x, y, size) ->
            val alpha = 0.1f + (index % 3) * 0.05f // Varia entre 0.1f e 0.2f

            Box(
                modifier = Modifier
                    .size(size)
                    .offset(x = x, y = y)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFFFD700).copy(alpha = alpha)
                    )
            )
        }
    }
}