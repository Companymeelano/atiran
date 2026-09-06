package ir.atiran.hamrah.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.grouped
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import kotlin.math.max

/**
 * «گوی آماری» — حلقه پیشرفت متحرک با گرادیانت + محتوای دلخواه در مرکز.
 */
@Composable
fun StatOrb(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 104.dp,
    trackColor: Color = Color(0x22FFFFFF),
    content: @Composable () -> Unit = {}
) {
    var started by remember { mutableStateOf(false) }
    val animated by animateFloatAsState(
        targetValue = if (started) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing),
        label = "orbProgress"
    )
    LaunchedEffect(Unit) { started = true }

    Box(modifier = modifier.size(sizeDp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 9.dp.toPx()
            val inset = stroke / 2 + 1.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            // مسیر زمینه
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // حلقه پیشرفت با گرادیانت
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(color.copy(alpha = 0.45f), color, color.copy(alpha = 0.55f), color.copy(alpha = 0.45f))
                ),
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        content()
    }
}

/**
 * «شمارنده متحرک» — عدد از صفر تا مقدار موردنظر با انیمیشن بالا می‌رود.
 */
@Composable
fun NumberTicker(
    value: Double,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    fontSize: Int = 22,
    color: Color = ir.atiran.hamrah.ui.theme.TextPrimary
) {
    var started by remember { mutableStateOf(false) }
    val fraction by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "tick"
    )
    LaunchedEffect(Unit) { started = true }
    val current = value * fraction
    Text(
        text = "$prefix${current.grouped()}$suffix",
        color = color,
        fontFamily = Vazirmatn,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        modifier = modifier
    )
}

/**
 * نوار پیشرفت گرادیانتی با هاله نور
 */
@Composable
fun GradientBar(
    progress: Float,
    brush: Brush,
    modifier: Modifier = Modifier,
    heightDp: Int = 10,
    trackColor: Color = Color(0x1FFFFFFF)
) {
    var started by remember { mutableStateOf(false) }
    val animated by animateFloatAsState(
        targetValue = if (started) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "bar"
    )
    LaunchedEffect(Unit) { started = true }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(CircleShape)
                .drawBehind {
                    drawRoundRect(brush = brush, cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f))
                }
        )
    }
}

/**
 * نمایشگر بزرگ آمار (عدد + برچسب + آیکن)
 */
@Composable
fun BigStat(
    label: String,
    value: String,
    sublabel: String? = null,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = Vazirmatn,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = accent,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        if (sublabel != null) {
            Spacer(Modifier.height(2.dp))
            Text(text = sublabel, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
        }
    }
}

/** سطر ترکیبی آمار کوچک داخل کارت قهرمان داشبورد */
@Composable
fun HeroStatColumn(label: String, value: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(tint)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "$label: ",
            color = TextSecondary,
            fontFamily = Vazirmatn,
            fontSize = 12.sp
        )
        Text(
            value,
            color = tint,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

/** حداکثر ساده برای جلوگیری از تقسیم بر صفر */
fun safeRatio(part: Double, whole: Double): Float =
    if (whole <= 0.0) 0f else (part / whole).toFloat().coerceIn(0f, 1f)

fun Double.positiveOrZero(): Double = max(0.0, this)
