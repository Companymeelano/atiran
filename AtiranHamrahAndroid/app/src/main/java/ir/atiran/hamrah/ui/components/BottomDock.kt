package ir.atiran.hamrah.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.ReceiptLong
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Gold500
import ir.atiran.hamrah.ui.theme.Gold700
import ir.atiran.hamrah.ui.theme.StrokeWhite
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * «داک پایین» — ناوبری اصلی برنامه؛ شیشه‌ای، معلق و با دکمه شناور مرکزی
 * برای ثبت سریع فاکتور جدید.
 */
data class DockItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomDock(
    currentRoute: String?,
    onSelect: (String) -> Unit,
    onFab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        DockItem("home", "خانه", Icons.Rounded.Home),
        DockItem("customers", "مشتریان", Icons.Rounded.Groups),
        DockItem("invoices", "فاکتورها", Icons.Rounded.ReceiptLong),
        DockItem("more", "بیشتر", Icons.Rounded.MenuBook)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp)
            .height(100.dp)
    ) {
        // بدنه داک
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xE60E1526))
                .drawBehind {
                    drawRoundRect(
                        color = StrokeWhite,
                        style = Stroke(1.dp.toPx()),
                        cornerRadius = CornerRadius(26.dp.toPx())
                    )
                }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockButton(items[0], currentRoute == items[0].route, Modifier.weight(1f)) { onSelect(items[0].route) }
            DockButton(items[1], currentRoute == items[1].route, Modifier.weight(1f)) { onSelect(items[1].route) }
            Spacer(Modifier.weight(0.95f)) // جای دکمه شناور مرکز
            DockButton(items[2], currentRoute == items[2].route, Modifier.weight(1f)) { onSelect(items[2].route) }
            DockButton(items[3], currentRoute == items[3].route, Modifier.weight(1f)) { onSelect(items[3].route) }
        }

        // دکمه شناور مرکزی «فاکتور جدید»
        val interaction = remember { MutableInteractionSource() }
        val pressed = interaction.collectIsPressedAsState()
        val fabScale by animateFloatAsState(
            targetValue = if (pressed.value) 0.92f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "fab"
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .scale(fabScale)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Gold400, Gold500)))
                .drawBehind {
                    // حلقه بیرونی طلایی تیره (عمق)
                    drawCircle(color = Gold700, style = Stroke(5.dp.toPx()))
                    drawCircle(color = Color.White.copy(alpha = 0.35f), style = Stroke(1.dp.toPx()))
                }
                .clickable(interactionSource = interaction, indication = null) { onFab() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Add, contentDescription = "فاکتور جدید", tint = Color(0xFF3D2A00), modifier = Modifier.size(26.dp))
                Text(
                    "فاکتور",
                    color = Color(0xFF3D2A00),
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun DockButton(
    item: DockItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dockScale"
    )
    Column(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 30.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) Teal500.copy(alpha = 0.22f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) Teal500 else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = item.label,
            color = if (selected) Teal500 else TextSecondary,
            fontFamily = Vazirmatn,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}
