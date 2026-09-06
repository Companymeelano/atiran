package ir.atiran.hamrah.ui.screens.invoices

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle2
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.InfoRow
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Info
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Warn
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * جزئیات فاکتور: اقلام، مالیات، عوارض، پرداختی و مانده
 */
@Composable
fun InvoiceDetailScreen(vm: MainViewModel, shfacfo: Long, onBack: () -> Unit) {
    val invoices by vm.invoices.collectAsState()
    val invoice = invoices.firstOrNull { it.shfacfo == shfacfo }

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppBarGlass(title = if (invoice?.isPre == true) "جزئیات پیش‌فاکتور" else "جزئیات فاکتور", onBack = onBack)

            if (invoice == null) {
                EmptyState(
                    icon = Icons.Rounded.RequestQuote,
                    title = "سند یافت نشد",
                    subtitle = "این فاکتور در فهرست موجود نیست"
                )
                return@Column
            }

            Column(Modifier.padding(horizontal = 18.dp)) {
                /* ---------- کارت سربرگ ---------- */
                GlassCard(Modifier.fillMaxWidth(), cornerRadius = 26) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(
                                        Brush.linearGradient(
                                            if (invoice.isPre) listOf(Color(0xFF334064), Color(0xFF1D2542))
                                            else listOf(Color(0xFF00564C), Color(0xFF003832))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (invoice.isPre) Icons.Rounded.RequestQuote else Icons.Rounded.ShoppingBag,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    invoice.customerName,
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    "شماره ${invoice.shfacfo} • ${invoice.date}" +
                                            (invoice.time?.let { " ساعت $it" } ?: ""),
                                    color = TextSecondary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 11.sp
                                )
                            }
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
                }

                Spacer(Modifier.height(14.dp))

                /* ---------- اقلام ---------- */
                Text(
                    "اقلام فاکتور (${invoice.lines.size} ردیف)",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(10.dp))

                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(6.dp)) {
                        // سرستون‌ها
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("کالا", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp, modifier = Modifier.weight(1.4f))
                            Text("تعداد", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp, modifier = Modifier.width(52.dp))
                            Text("قیمت", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp, modifier = Modifier.width(84.dp))
                            Text("جمع", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.sp, modifier = Modifier.width(96.dp))
                        }
                        invoice.lines.forEach { line ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1.4f)) {
                                    Text(
                                        line.name,
                                        color = TextPrimary,
                                        fontFamily = Vazirmatn,
                                        fontSize = 12.sp
                                    )
                                    if (line.tafifLine > 0) {
                                        Text(
                                            "تخفیف: ${line.tafifLine.money()}",
                                            color = Warn,
                                            fontFamily = Vazirmatn,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Text(
                                    ir.atiran.hamrah.core.util.toPersianDigits(
                                        if (line.qty == line.qty.toLong().toDouble())
                                            line.qty.toLong().toString()
                                        else line.qty.toString()
                                    ),
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 12.sp,
                                    modifier = Modifier.width(52.dp)
                                )
                                Text(
                                    line.unitPrice.money(),
                                    color = TextSecondary,
                                    fontFamily = Vazirmatn,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.width(84.dp)
                                )
                                Text(
                                    line.lineSum.money(),
                                    color = Teal500,
                                    fontFamily = Vazirmatn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.width(96.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                /* ---------- جمع‌بندی مالی ---------- */
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("جمع کل اقلام", invoice.sumAll.money())
                        if (invoice.tafif > 0) InfoRow("تخفیف", invoice.tafif.money(), valueColor = Warn)
                        if (invoice.tax > 0) InfoRow("مالیات بر ارزش افزوده", invoice.tax.money())
                        if (invoice.avarez > 0) InfoRow("عوارض", invoice.avarez.money())
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x24FFFFFF))
                        )
                        Spacer(Modifier.height(6.dp))
                        InfoRow("مبلغ قابل پرداخت", invoice.total.money(), valueColor = Teal500)
                        InfoRow("پرداخت‌شده", invoice.paid.money(), valueColor = Success)
                        InfoRow(
                            "مانده",
                            invoice.remaining.money(),
                            valueColor = if (invoice.remaining > 0) Danger else Success
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                if (!invoice.isPre && invoice.status == InvoiceStatus.CONFIRMED) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            AtiranButton3D(
                                text = "ثبت وصولی",
                                icon = Icons.Rounded.CheckCircle2,
                                style = Btn3DStyle.PRIMARY,
                                height = 50.dp,
                                onClick = { /* وصولی از بخش پیش‌دریافت انجام می‌شود */ }
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            AtiranButton3D(
                                text = "مشاهده مشتری",
                                style = Btn3DStyle.GHOST,
                                height = 50.dp,
                                onClick = onBack
                            )
                        }
                    }
                }

                Spacer(Modifier.height(110.dp))
            }
        }
    }
}
