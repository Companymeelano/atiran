package ir.atiran.hamrah.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * «کاشی سه‌بعدی منو» — المان اصلی منوی خلاقانه برنامه.
 * هر کاشی: گرادیانت اختصاصی + هاله نور داخلی + قاب براق بالایی +
 * آیکن داخل دایره شیشه‌ای؛ با فشردن، کاشی مثل جسم واقعی فرو می‌رود.
 */
data class TileData(
    val route: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val gradient: List<Color>,
    val glow: Color,
    val badge: String? = null
)

@Composable
fun Tile3D(
    tile: TileData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    aspect: Float = 1.05f
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tileScale"
    )

    Box(
        modifier = modifier
            .aspectRatio(aspect)
            .scale(scale)
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(tile.gradient))
            .drawBehind {
                // هاله نور داخلی (گلو)
                drawCircle(
                    color = tile.glow.copy(alpha = 0.22f),
                    radius = size.minDimension * 0.85f,
                    center = Offset(size.width * 0.18f, size.height * 0.12f)
                )
                // قاب براق بالای کاشی (حس سه‌بعدی)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.26f), Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.55f
                    ),
                    style = Stroke(width = 1.5.dp.toPx()),
                    cornerRadius = CornerRadius(26.dp.toPx())
                )
                // سایه داخلی پایین برای عمق بیشتر
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f)),
                        startY = size.height * 0.6f,
                        endY = size.height
                    ),
                    cornerRadius = CornerRadius(26.dp.toPx())
                )
            }
            .clickable(interactionSource = interaction, indication = null) { onClick(tile.route) }
            .padding(14.dp)
    ) {
        if (tile.badge != null) {
            Text(
                text = tile.badge,
                color = Color(0xFF03201C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Vazirmatn,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color(0xFF9FF5E5))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Text(
                text = tile.title,
                color = Color.White,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (tile.subtitle != null) {
                Spacer(Modifier.padding(top = 1.dp))
                Text(
                    text = tile.subtitle,
                    color = Color.White.copy(alpha = 0.72f),
                    fontFamily = Vazirmatn,
                    fontSize = 10.5.sp,
                    maxLines = 1
                )
            }
            Spacer(Modifier.padding(top = 10.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tile.icon,
                    contentDescription = tile.title,
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
    }
}

/**
 * شبکه منوی دو ستونه — بدون LazyVerticalGrid تا داخل ستون‌های اسکرول‌شو
 * بدون هیچ تداخلی کار کند.
 */
@Composable
fun MenuGrid(
    tiles: List<TileData>,
    onTile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Tile3D(tile = row[0], onClick = onTile, modifier = Modifier.weight(1f))
                if (row.size > 1) {
                    Tile3D(tile = row[1], onClick = onTile, modifier = Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
