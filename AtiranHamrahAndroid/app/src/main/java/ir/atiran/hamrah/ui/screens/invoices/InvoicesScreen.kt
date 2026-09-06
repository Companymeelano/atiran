package ir.atiran.hamrah.ui.screens.invoices

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.RequestQuote
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
import ir.atiran.hamrah.data.model.Invoice
import ir.atiran.hamrah.data.model.InvoiceStatus
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Info
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Warn
import ir.atiran.hamrah.ui.theme.Vazirmatn

private val FILTERS = listOf("همه", "تسویه‌نشده", "پیش‌فاکتور", "در انتظار تأیید")

/**
 * فهرست فاکتورها و پیش‌فاکتورها
 */
@Composable
fun InvoicesScreen(vm: MainViewModel, onOpenInvoice: (Long) -> Unit, onNewInvoice: () -> Unit) {
    val invoices by vm.invoices.collectAsState()
    var filter by remember { mutableStateOf("همه") }

    val filtered = invoices.filter {
        when (filter) {
            "تسویه‌نشده" -> !it.isSettled
            "پیش‌فاکتور" -> it.isPre
            "در انتظار تأیید" -> it.status == InvoiceStatus.PENDING
            else -> true
        }
    }

    val totalSales = invoices.filter { !it.isPre }.sumOf { it.total }
    val totalRemaining = invoices.filter { !it.isPre }.sumOf { it.remaining }

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
                    "فاکتورها",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.weight(1f))
                StatusChip("${invoices.size} سند", Teal500)
            }

            Spacer(Modifier.height(12.dp))

            // خلاصه مالی
            GlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("مجموع فروش", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            totalSales.money(),
                            color = Teal500,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        Modifier
                            .size(1.dp, 30.dp)
                            .background(Color(0x33FFFFFF))
                    )
                    Column {
                        Text("مانده مطالبات", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            totalRemaining.money(),
                            color = if (totalRemaining > 0) Danger else Success,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FILTERS.forEach { f ->
                    FilterPill(f, filter == f) { filter = f }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.ReceiptLong,
                    title = "فاکتوری یافت نشد",
                    subtitle = "اولین فاکتور امروز را ثبت کنید"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                ) {
                    items(filtered, key = { "${it.shfacfo}-${it.isPre}" }) { invoice ->
                        InvoiceRow(invoice) { onOpenInvoice(invoice.shfacfo) }
                    }
                }
            }
        }

        // دکمه شناور ثبت فاکتور
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 110.dp)
        ) {
            AtiranButton3D(
                text = "ثبت فاکتور جدید",
                icon = Icons.Rounded.AddShoppingCart,
                style = Btn3DStyle.GOLD,
                fillWidth = false,
                onClick = onNewInvoice
            )
        }
    }
}

@Composable
private fun InvoiceRow(invoice: Invoice, onClick: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // نشان نوع سند
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                if (invoice.isPre) listOf(Color(0xFF334064), Color(0xFF1D2542))
                                else listOf(Color(0xFF00564C), Color(0xFF003832))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (invoice.isPre) Icons.Rounded.RequestQuote else Icons.Rounded.ReceiptLong,
                        contentDescription = null,
                        tint = if (invoice.isPre) Info else Teal500,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        invoice.customerName,
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "شماره ${invoice.shfacfo} • ${invoice.date}" +
                                (invoice.time?.let { " • $it" } ?: ""),
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 11.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        invoice.total.money(),
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    StatusChip(
                        when {
                            invoice.isPre -> "پیش‌فاکتور"
                            invoice.status == InvoiceStatus.PENDING -> "در انتظار تأیید"
                            invoice.isSettled -> "تسویه‌شده"
                            else -> "مانده‌دار"
                        },
                        when {
                            invoice.isPre -> Info
                            invoice.status == InvoiceStatus.PENDING -> Warn
                            invoice.isSettled -> Success
                            else -> Danger
                        }
                    )
                }
            }
            // نوار نسبت پرداخت
            if (!invoice.isSettled && !invoice.isPre) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x1FFFFFFF))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(
                                (invoice.paid / invoice.total).toFloat().coerceIn(0.02f, 1f)
                            )
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(listOf(Success, Teal500)))
                    )
                }
            }
        }
    }
}
