package ir.atiran.hamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.initials
import ir.atiran.hamrah.ui.theme.GlassWhite
import ir.atiran.hamrah.ui.theme.StrokeWhite
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * «کارت شیشه‌ای» — سطح اصلی نمایش محتوا؛
 * نیمه‌شفاف با قاب نوری و درخشش مورب.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 22,
    containerColor: Color = GlassWhite,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(containerColor)
            .drawBehind {
                // قاب نوری
                drawRoundRect(
                    color = StrokeWhite,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius.dp.toPx())
                )
                // درخشش مورب گوشه بالا-چپ
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.07f), Color.Transparent),
                        start = Offset(0f, 0f),
                        end = Offset(size.width * 0.6f, size.height * 0.6f)
                    ),
                    cornerRadius = CornerRadius(cornerRadius.dp.toPx())
                )
            }
    ) {
        content()
    }
}

/** آواتار دایره‌ای با حروف اول نام و گرادیانت */
@Composable
fun AvatarCircle(
    name: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 46,
    gradient: List<Color> = listOf(Color(0xFF00A38F), Color(0xFF00564C))
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.initials(),
            color = Color.White,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = (sizeDp / 3.2).sp
        )
    }
}

/** چیپ وضعیت رنگی */
@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    Text(
        text = text,
        color = if (filled) Color(0xFF0A0F1E) else color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Vazirmatn,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (filled) color else color.copy(alpha = 0.16f))
            .drawBehind {
                if (!filled) {
                    drawRoundRect(
                        color = color.copy(alpha = 0.45f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(0.75.dp.toPx()),
                        cornerRadius = CornerRadius(50.dp.toPx() * 0.4f)
                    )
                }
            }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** سرصفحه بخش با دکمه عملیات در انتهای آن */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(width = 4.dp, height = 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(brush = Brush.verticalGradient(listOf(Color(0xFF00C9B1), Color(0xFF00564C))))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            color = ir.atiran.hamrah.ui.theme.TextPrimary,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/** آیکن دایره‌ای شیشه‌ای قابل کلیک */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = ir.atiran.hamrah.ui.theme.TextPrimary,
    background: Color = GlassWhite,
    sizeDp: Int = 40
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(sizeDp.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(background)
            .drawBehind {
                drawRoundRect(
                    color = StrokeWhite,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()),
                    cornerRadius = CornerRadius(size.width / 2f)
                )
            }
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}

/** جداکننده کم‌رنگ */
@Composable
fun HairDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(StrokeWhite)
    )
}

/** آیتم «مقدار: برچسب» */
@Composable
fun InfoRow(label: String, value: String, valueColor: Color = ir.atiran.hamrah.ui.theme.TextPrimary) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        Text(
            value,
            color = valueColor,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

/** ستون اطلاعات کوچک داخل کارت‌ها */
@Composable
fun MiniStat(label: String, value: String, icon: ImageVector? = null, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(4.dp))
        }
        Text(
            value,
            color = ir.atiran.hamrah.ui.theme.TextPrimary,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(label, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
    }
}
