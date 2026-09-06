package ir.atiran.hamrah.ui.screens.splash

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.R
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space950
import ir.atiran.hamrah.ui.theme.Teal400
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import kotlinx.coroutines.delay

/**
 * صفحه آغازین: لوگوی سه‌بعدی با انیمیشن ورود + شعار برنامه
 */
@Composable
fun SplashScreen(
    booting: Boolean,
    loggedIn: Boolean,
    onDone: (Boolean) -> Unit
) {
    var logoVisible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.3f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutBack),
        label = "logo"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "logoAlpha"
    )

    // نقطه‌های بارگذاری
    val transition = rememberInfiniteTransition(label = "dots")
    val dotPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
        label = "dot"
    )

    // مقادیر تازه برای جلوگیری از مقدار قدیمی (stale) در کوروتین
    val currentBooting by rememberUpdatedState(booting)
    val currentLoggedIn by rememberUpdatedState(loggedIn)

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(1500)
        while (currentBooting) delay(150)
        onDone(currentLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Space950)
    ) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // هاله پشت لوگو + لوگو
            Box(
                modifier = Modifier.size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .alpha(logoAlpha * 0.55f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0x4700C9B1), Color.Transparent)
                            )
                        )
                )
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "آتیران همراه",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(138.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )
            }

            Spacer(Modifier.height(26.dp))

            Text(
                text = "آتیران همراه",
                color = Color(0xFFEAF0FF),
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                modifier = Modifier.alpha(logoAlpha)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "همراه هوشمند فروش و ویزیت",
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 14.sp,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(Modifier.height(44.dp))

            // سه نقطه متحرک بارگذاری
            Row(
                Modifier.alpha(logoAlpha),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val active = dotPhase.toInt() == index
                    Box(
                        Modifier
                            .size(if (active) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (index == 1) Gold400 else Teal400)
                            .alpha(if (active) 1f else 0.35f)
                    )
                }
            }
        }
    }
}
