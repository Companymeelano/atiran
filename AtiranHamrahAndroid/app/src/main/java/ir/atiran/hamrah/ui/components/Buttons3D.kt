package ir.atiran.hamrah.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.InteractionSource
import androidx.compose.foundation.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Gold500
import ir.atiran.hamrah.ui.theme.Gold700
import ir.atiran.hamrah.ui.theme.Teal600
import ir.atiran.hamrah.ui.theme.Teal800
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * «دکمه سه‌بعدی آتیران» — دکمه امضای برنامه.
 * یک وجه اصلی با گرادیانت + لایه اکستروژن (عمق) زیر آن که هنگام فشردن
 * انگار دکمه واقعاً به داخل صفحه فرو می‌رود؛ بدون هیچ کتابخانه خارجی.
 */
enum class Btn3DStyle(
    val faceTop: Color,
    val faceBottom: Color,
    val extrude: Color,
    val contentColor: Color
) {
    PRIMARY(Teal600, Color(0xFF00937F), Color(0xFF00443B), Color(0xFFEFFFFC)),
    GOLD(Gold400, Gold500, Gold700, Color(0xFF3D2A00)),
    DANGER(Color(0xFFFF7A7A), Color(0xFFE14D4D), Color(0xFF7A1F1F), Color(0xFFFFF5F5)),
    GHOST(Color(0x1FFFFFFF), Color(0x10FFFFFF), Color(0x14FFFFFF), Color(0xFFEAF0FF))
}

@Composable
fun AtiranButton3D(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: Btn3DStyle = Btn3DStyle.PRIMARY,
    enabled: Boolean = true,
    fillWidth: Boolean = true,
    height: Dp = 56.dp
) {
    val interaction: InteractionSource = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val maxDepth = 9.dp
    val depth by animateDpAsState(
        targetValue = if (pressed) 2.dp else maxDepth,
        animationSpec = tween(durationMillis = 110),
        label = "btnDepth"
    )

    Box(
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height + maxDepth)
            .alpha(if (enabled) 1f else 0.45f)
            .drawBehind {
                val d = depth.toPx()
                val faceH = height.toPx()
                val shift = d - maxDepth.toPx()
                // لایه عمق (اکستروژن)
                drawRoundRect(
                    color = style.extrude,
                    topLeft = Offset(0f, shift + maxDepth.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width, faceH),
                    cornerRadius = CornerRadius(20.dp.toPx())
                )
                // سایه نرم زیر اکستروژن
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f)),
                        startY = shift + maxDepth.toPx() + faceH,
                        endY = shift + maxDepth.toPx() + faceH + 6.dp.toPx()
                    ),
                    topLeft = Offset(0f, shift + maxDepth.toPx() + faceH),
                    size = androidx.compose.ui.geometry.Size(size.width, 6.dp.toPx()),
                    cornerRadius = CornerRadius(20.dp.toPx())
                )
                // وجه اصلی
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(style.faceTop, style.faceBottom)),
                    topLeft = Offset(0f, shift),
                    size = androidx.compose.ui.geometry.Size(size.width, faceH),
                    cornerRadius = CornerRadius(20.dp.toPx())
                )
                // خط نور بالای دکمه (حس براق سه‌بعدی)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.30f), Color.Transparent),
                        startY = shift + 2.dp.toPx(),
                        endY = shift + faceH * 0.45f
                    ),
                    topLeft = Offset(6.dp.toPx(), shift + 2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 12.dp.toPx(), faceH * 0.45f),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .graphicsLayer {
                    translationY = (depth - maxDepth).toPx()
                }
                .height(height)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = style.contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = style.contentColor,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
