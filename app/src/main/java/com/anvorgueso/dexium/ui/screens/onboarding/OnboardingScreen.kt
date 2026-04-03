package com.anvorgueso.dexium.ui.screens.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val useHdImages by viewModel.useHdImages.collectAsState()
    val glass = DexiumGlass.colors

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect { onNavigateToHome() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "onboarding_orbs")
    val orbOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse),
        label = "orb1"
    )
    val orbOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -20f,
        animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Reverse),
        label = "orb2"
    )
    val orbAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "orbAlpha"
    )

    GradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glass.accent.copy(alpha = orbAlpha),
                            glass.accent.copy(alpha = orbAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f + orbOffset1, size.height * 0.15f + orbOffset2),
                        radius = size.width * 0.35f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7B61FF).copy(alpha = orbAlpha * 0.8f),
                            Color(0xFF7B61FF).copy(alpha = orbAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.15f - orbOffset2, size.height * 0.75f - orbOffset1),
                        radius = size.width * 0.4f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4FC3F7).copy(alpha = orbAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f + orbOffset2, size.height * 0.45f),
                        radius = size.width * 0.25f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.onboarding_welcome),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.onboarding_choose),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImageQualityOption(
                        title = stringResource(R.string.onboarding_animated_title),
                        description = stringResource(R.string.onboarding_animated_desc),
                        icon = Icons.Default.Hd,
                        isSelected = useHdImages,
                        onClick = { viewModel.selectImageQuality(true) },
                        modifier = Modifier.weight(1f),
                        glowColor = glass.accent
                    )

                    ImageQualityOption(
                        title = stringResource(R.string.onboarding_artwork_title),
                        description = stringResource(R.string.onboarding_artwork_desc),
                        icon = Icons.Default.Image,
                        isSelected = !useHdImages,
                        onClick = { viewModel.selectImageQuality(false) },
                        modifier = Modifier.weight(1f),
                        glowColor = Color(0xFF7B61FF)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.onboarding_change_later),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                GlassButton(
                    text = stringResource(R.string.onboarding_get_started),
                    onClick = { viewModel.completeOnboarding() },
                    accentColor = glass.accent
                )
            }
        }
    }
}

@Composable
private fun GlassButton(
    text: String,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.6f),
                        accentColor.copy(alpha = 0.2f)
                    )
                ),
                shape = shape
            )
            .drawWithContent {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.25f),
                            accentColor.copy(alpha = 0.1f)
                        )
                    )
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.5f
                    )
                )
                drawContent()
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = Color.White
        )
    }
}

@Composable
private fun ImageQualityOption(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Cyan
) {
    val glass = DexiumGlass.colors
    val borderColor = if (isSelected) glowColor else Color.Transparent
    val shape = RoundedCornerShape(20.dp)

    GlassCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.8f),
                            borderColor.copy(alpha = 0.3f)
                        )
                    ),
                    shape = shape
                ) else Modifier
            ),
        cornerRadius = 20.dp,
        glassAlpha = if (isSelected) 0.14f else 0.06f,
        glowColor = if (isSelected) glowColor else null,
        contentPadding = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) glowColor else TextSecondary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}
