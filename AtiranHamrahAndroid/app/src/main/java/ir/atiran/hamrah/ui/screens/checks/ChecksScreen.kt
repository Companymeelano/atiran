package ir.atiran.hamrah.ui.screens.checks

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
import androidx.compose.material.icons.rounded.Payments
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
import ir.atiran.hamrah.data.model.CheckStatus
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.InfoRow
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import ir.atiran.hamrah.ui.theme.Warn

private val TABS = listOf("همه", "در جیب", "به بانک سپرده‌شده", "وصول‌شده", "برگشتی")

/**
 * چک‌های دریافتی با وضعیت و سررسید
 */
@Composable
fun ChecksScreen(vm: MainViewModel) {
    val checks by vm.checks.collectAsState()
    var tab by remember { mutableStateOf("همه") }

    val filtered = checks.filter {
        when (tab) {
            "همه" -> true
            else -> it.status.label == tab
        }
    }
    val inPocketValue = checks.filter { it.status == CheckStatus.IN_POCKET }.sumOf { it.amount }
    val bounced = checks.count { it.status == CheckStatus.BOUNCED }

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
                    "چک‌ها",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.weight(1f))
                if (bounced > 0) StatusChip("$bounced برگشتی", Danger)
            }

            Spacer(Modifier.height(12.dp))

            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("ارزش چک‌های در جیب", inPocketValue.money(), valueColor = Teal500)
                    InfoRow(
                        "کل چک‌های در جریان",
                        checks.filter { it.status == CheckStatus.IN_POCKET || it.status == CheckStatus.DEPOSITED }
                            .sumOf { it.amount }.money(),
                        valueColor = Success
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val tabsScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(tabsScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TABS.forEach { t -> FilterPill(t, tab == t) { tab = t } }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Payments,
                    title = "چکی در این وضعیت نیست",
                    subtitle = "وضعیت دیگری را انتخاب کنید"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                ) {
                    items(filtered, key = { it.rdf }) { check ->
                        CheckRow(check)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckRow(check: ir.atiran.hamrah.data.model.CheckItem) {
    val statusColor = when (check.status) {
        CheckStatus.CASHED -> Success
        CheckStatus.BOUNCED -> Danger
        CheckStatus.RETURNED -> Warn
        else -> Teal500
    }
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF5C1F6B), Color(0xFF371242)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Payments, null, tint = Color(0xFFB57BFF), modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        check.customerName,
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${check.bankName}${check.shobe?.let { " • $it" } ?: ""}",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 10.5.sp
                    )
                }
                StatusChip(check.status.label, statusColor)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x0FFFFFFF))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "شماره ${check.checkNo}",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 11.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "سررسید ${check.dueDate}",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 11.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    check.amount.money(),
                    color = statusColor,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }
        }
    }
}
