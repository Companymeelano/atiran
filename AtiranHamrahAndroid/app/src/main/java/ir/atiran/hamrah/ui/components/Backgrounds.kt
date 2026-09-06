package ir.atiran.hamrah.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
/**
 * «پس‌زمینه شفق قطبی» — دو هاله رنگی که آرام در پس‌زمینه صفحه
 * شنا می‌کنند؛ حس عمق و زنده‌بودن برنامه.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    firstColor: Color = Color(0x3300C9B1),
    secondColor: Color = Color(0x2EF5A623),
    thirdColor: Color = Color(0x265B8DEF)
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 14000, easing = LinearEasing)),
        label = "auroraT"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // هاله فیروزه‌ای — بالا سمت راست
        val cx1 = w * (0.82f + 0.06f * sin(t * 2f * Math.PI.toFloat()))
        val cy1 = h * (0.12f + 0.04f * sin(t * 3f * Math.PI.toFloat() + 1f))
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(firstColor, Color.Transparent),
                center = Offset(cx1, cy1),
                radius = w * 0.75f
            ),
            size = size
        )

        // هاله طلایی — پایین سمت چپ
        val cx2 = w * (0.15f + 0.05f * sin(t * 2.5f * Math.PI.toFloat() + 2f))
        val cy2 = h * (0.85f + 0.05f * cos(t * 2f * Math.PI.toFloat()))        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(secondColor, Color.Transparent),
                center = Offset(cx2, cy2),
                radius = w * 0.7f
            ),
            size = size
        )

        // هاله آبی — مرکز صفحه
        val cx3 = w * (0.5f + 0.08f * sin(t * 1.6f * Math.PI.toFloat() + 4f))
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(thirdColor, Color.Transparent),
                center = Offset(cx3, h * 0.45f),
                radius = w * 0.65f
            ),
            size = size
        )
    }
}
