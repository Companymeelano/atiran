package ir.atiran.hamrah.ui.screens.customers

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.ShoppingCart
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.money
import ir.atiran.hamrah.core.util.toEnglishDigits
import ir.atiran.hamrah.data.model.Customer
import ir.atiran.hamrah.data.model.Invoice
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.AvatarCircle
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

private val TABS = listOf("خلاصه", "فاکتورها", "چک‌ها")

/**
 * پرونده مشتری: اطلاعات، اعتبار، فاکتورها و چک‌ها
 */
@Composable
fun CustomerDetailScreen(
    vm: MainViewModel,
    shmo: Int,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    val customers by vm.customers.collectAsState()
    val invoices by vm.invoices.collectAsState()
    val checks by vm.checks.collectAsState()
    val context = LocalContext.current

    val customer = customers.firstOrNull { it.shmo == shmo }
    var tab by remember { mutableStateOf("خلاصه") }

    if (customer == null) {
        Box(Modifier.fillMaxSize().background(Space900)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppBarGlass(title = "پرونده مشتری", onBack = onBack)
                EmptyState(
                    icon = Icons.Rounded.ReceiptLong,
                    title = "مشتری یافت نشد",
                    subtitle = "ممکن است حذف شده باشد"
                )
            }
        }
        return
    }

    val customerInvoices = invoices.filter { it.customerShmo == shmo }
    val customerChecks = checks.filter { it.customerShmo == shmo }

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
            AppBarGlass(title = "پرونده مشتری", onBack = onBack)

            Column(Modifier.padding(horizontal = 18.dp)) {
                /* ---------- کارت شناسنامه ---------- */
                GlassCard(Modifier.fillMaxWidth(), cornerRadius = 26) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(
                                name = customer.name,
                                sizeDp = 58,
                                gradient = if (customer.blackList)
                                    listOf(Color(0xFF8A2D2D), Color(0xFF4A1414))
                                else listOf(Color(0xFF00A38F), Color(0xFF00564C))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    customer.name,
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StatusChip(customer.code ?: "—", Info)
                                    Spacer(Modifier.width(6.dp))
                                    StatusChip(customer.groupName ?: "بدون گروه", Teal500)
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(Modifier.weight(1f)) {
                                AtiranButton3D(
                                    text = "فاکتور جدید",
                                    icon = Icons.Rounded.ShoppingCart,
                                    style = Btn3DStyle.GOLD,
                                    height = 48.dp,
                                    onClick = { onOpen("new_invoice?customer=${customer.shmo}") }
                                )
                            }
                            if (!customer.cell.isNullOrBlank()) {
                                GlassCallButton("تماس") {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_DIAL,
                                        android.net.Uri.parse("tel:${customer.cell.toEnglishDigits()}")
                                    )
                                    context.startActivity(intent)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // آمار مالی
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FinStat("مانده بدهی", customer.man.money(), if (customer.man > 0) Danger else Success)
                            FinStat("اعتبار", customer.cred.money(), Teal500)
                            FinStat("فاکتورها", "${customerInvoices.size}", Info)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                /* ---------- تب‌ها ---------- */
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TABS.forEach { t ->
                        val selected = tab == t
                        Text(
                            text = t,
                            color = if (selected) Color(0xFF0A0F1E) else TextSecondary,
                            fontFamily = Vazirmatn,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) Teal500 else Color(0x14FFFFFF))
                                .clickable { tab = t }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                when (tab) {
                    "خلاصه" -> SummaryTab(customer, customerInvoices)
                    "فاکتورها" -> InvoicesTab(customerInvoices, onOpen)
                    "چک‌ها" -> ChecksTab(customerChecks)
                }

                Spacer(Modifier.height(130.dp))
            }
        }
    }
}

@Composable
private fun GlassCallButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(57.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x14FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Call, contentDescription = label, tint = Teal500, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = TextPrimary, fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun FinStat(label: String, value: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = tint, fontFamily = Vazirmatn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(3.dp))
        Text(label, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 10.5.sp)
    }
}

@Composable
private fun SummaryTab(customer: Customer, invoices: List<Invoice>) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "اطلاعات تماس و ویزیت",
                color = TextPrimary,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(10.dp))
            InfoRow("آدرس", customer.address ?: "ثبت نشده")
            InfoRow("تلفن", customer.tel ?: "—")
            InfoRow("همراه", customer.cell ?: "—")
            InfoRow("روز ویزیت", customer.visitDay ?: "تعیین‌نشده")
            InfoRow(
                "وضعیت",
                when {
                    customer.blackList -> "لیست سیاه"
                    customer.justNaghdi -> "فقط نقدی"
                    else -> "فعال"
                },
                valueColor = when {
                    customer.blackList -> Danger
                    customer.justNaghdi -> Warn
                    else -> Success
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = Teal500, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    if (customer.lat != null) "موقعیت مکانی ثبت‌شده" else "موقعیت مکانی ثبت نشده",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.RequestQuote, null, tint = Info, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "آخرین خرید: " + (invoices.firstOrNull()?.date ?: "بدون سابقه"),
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InvoicesTab(invoices: List<Invoice>, onOpen: (String) -> Unit) {
    if (invoices.isEmpty()) {
        EmptyState(
            icon = Icons.Rounded.ReceiptLong,
            title = "فاکتوری ثبت نشده",
            subtitle = "با دکمه «فاکتور جدید» اولین فروش را ثبت کنید"
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        invoices.forEach { invoice ->
            GlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen("invoice/${invoice.shfacfo}") }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "شماره ${invoice.shfacfo}",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            if (invoice.isPre) StatusChip("پیش‌فاکتور", Info)
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "${invoice.date} • ${invoice.lines.size} ردیف",
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
                            if (invoice.isSettled) "تسویه‌شده" else "مانده ${invoice.remaining.money()}",
                            if (invoice.isSettled) Success else Danger
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecksTab(checks: List<ir.atiran.hamrah.data.model.CheckItem>) {
    if (checks.isEmpty()) {
        EmptyState(
            icon = Icons.Rounded.RequestQuote,
            title = "چکی ثبت نشده",
            subtitle = "چک‌های دریافتی این مشتری اینجا نمایش داده می‌شود"
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        checks.forEach { check ->
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${check.bankName} • ${check.checkNo}",
                            color = TextPrimary,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        StatusChip(check.status.label, when (check.status) {
                            ir.atiran.hamrah.data.model.CheckStatus.CASHED -> Success
                            ir.atiran.hamrah.data.model.CheckStatus.BOUNCED -> Danger
                            else -> Warn
                        })
                    }
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Text(
                            "مبلغ: ${check.amount.money()}",
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 11.5.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "سررسید: ${check.dueDate}",
                            color = TextSecondary,
                            fontFamily = Vazirmatn,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }
    }
}
