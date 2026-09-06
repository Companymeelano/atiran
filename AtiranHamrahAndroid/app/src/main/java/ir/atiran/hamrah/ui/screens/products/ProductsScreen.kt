package ir.atiran.hamrah.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.money
import ir.atiran.hamrah.core.util.toPersianDigits
import ir.atiran.hamrah.data.model.Kala
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.SearchFieldGlass
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Warn
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * کالا و انبار: گروه‌ها، قیمت و موجودی با هشدار نقطه سفارش
 */
@Composable
fun ProductsScreen(vm: MainViewModel) {
    val kalas by vm.kalas.collectAsState()
    val groups by vm.kalaGroups.collectAsState()
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("همه") }

    val filtered = kalas.filter {
        val matchGroup = group == "همه" || it.groupName == group
        val matchQuery = query.isBlank() || it.name.contains(query, true) || it.code.contains(query, true)
        matchGroup && matchQuery
    }
    val lowStock = kalas.count { it.stockVah < it.reorderPoint }
    val totalValue = kalas.sumOf { it.stockVah * it.price }

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "کالا و انبار",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.weight(1f))
                if (lowStock > 0) {
                    StatusChip("$lowStock کالای کم‌موجود", Danger)
                }
            }

            Spacer(Modifier.height(12.dp))

            // خلاصه انبار
            GlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("تعداد اقلام", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            kalas.size.toPersianDigits(),
                            color = Teal500,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("گروه‌ها", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            groups.size.toPersianDigits(),
                            color = Teal500,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ارزش موجودی", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "${(totalValue / 1_000_000_000.0).let { if (it >= 1.0) "${it.toInt()} میلیارد" else "${(totalValue / 1_000_000).toInt()} میلیون" }} تومان",
                            color = Gold400,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            SearchFieldGlass(query, { query = it }, "جستجوی نام یا کد کالا...")

            Spacer(Modifier.height(10.dp))

            // چیپ گروه‌ها — اسکرول افقی
            val groupScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(groupScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterPill("همه", group == "همه") { group = "همه" }
                groups.forEach { g ->
                    FilterPill(g.name, group == g.name) { group = g.name }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Inventory2,
                    title = "کالایی یافت نشد",
                    subtitle = "جستجو یا گروه دیگری را امتحان کنید"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                ) {
                    items(filtered, key = { it.shka }) { kala ->
                        KalaRow(kala)
                    }
                }
            }
        }
    }
}

@Composable
private fun KalaRow(kala: Kala) {
    val low = kala.stockVah < kala.reorderPoint
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7A4E00), Color(0xFF4A2E00))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        kala.code.take(2),
                        color = Gold400,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        kala.name,
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${kala.groupName} • کد ${kala.code} • ${kala.anbarName ?: "—"}",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 10.sp
                    )
                }
                Text(
                    kala.price.money(),
                    color = Gold400,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // نوار موجودی
            Row(verticalAlignment = Alignment.CenterVertically) {
                val ratio = if (kala.reorderPoint > 0)
                    (kala.stockVah / (kala.reorderPoint * 3.0)).toFloat().coerceIn(0.03f, 1f)
                else 0.6f
                Box(
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x1FFFFFFF))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(ratio)
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    if (low) listOf(Danger, Color(0xFFFF9E57))
                                    else listOf(Success, Teal500)
                                )
                            )
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "موجودی ${kala.stockVah.toPersianDigits()} ${kala.unit}",
                    color = if (low) Danger else TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 10.5.sp
                )
                if (low) {
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Rounded.WarningAmber, null, tint = Warn, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
