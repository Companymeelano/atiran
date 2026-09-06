package ir.atiran.hamrah.ui.screens.prepayments

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditScore
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
import ir.atiran.hamrah.data.model.PrepayType
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.screens.customers.GlassTextField
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import ir.atiran.hamrah.ui.theme.Warn

/**
 * پیش‌دریافت‌ها (معادل PishDaryaft) + فرم ثبت پیش‌دریافت جدید
 */
@Composable
fun PrepaymentsScreen(vm: MainViewModel) {
    val prepayments by vm.prepayments.collectAsState()
    val customers by vm.customers.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    val todayTotal = prepayments.sumOf { it.amount }

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "پیش‌دریافت‌ها",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    if (showForm) "بستن فرم" else "ثبت جدید",
                    color = Color(0xFF0A0F1E),
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Gold400)
                        .clickable { showForm = !showForm }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (showForm) {
                PrepaymentForm(
                    customers = customers,
                    onDismiss = { showForm = false },
                    onSubmit = { customer, amount, type, note ->
                        vm.addPrepayment(customer, amount, type, note)
                        showForm = false
                    }
                )
            } else {
                /* ---------- خلاصه ---------- */
                GlassCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("جمع دریافتی‌های ثبت‌شده", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 11.5.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                todayTotal.money(),
                                color = Success,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        AtiranButton3D(
                            text = "ثبت پیش‌دریافت",
                            icon = Icons.Rounded.Add,
                            style = Btn3DStyle.GOLD,
                            fillWidth = false,
                            height = 48.dp,
                            onClick = { showForm = true }
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (prepayments.isEmpty()) {
                    EmptyState(
                        icon = Icons.Rounded.AccountBalanceWallet,
                        title = "پیش‌دریافتی ثبت نشده",
                        subtitle = "دریافتی‌های نقدی، کارت‌خوان و چک را همین‌جا ثبت کنید"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                    ) {
                        items(prepayments, key = { it.id }) { pre ->
                            GlassCard(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFF0F5132), Color(0xFF08301E))
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                when (pre.type) {
                                                    PrepayType.CASH -> Icons.Rounded.AccountBalanceWallet
                                                    PrepayType.POS -> Icons.Rounded.CreditScore
                                                    PrepayType.CHECK -> Icons.Rounded.RequestQuote
                                                },
                                                contentDescription = null,
                                                tint = Success,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                pre.customerName,
                                                color = TextPrimary,
                                                fontFamily = Vazirmatn,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                "${pre.date}${pre.note?.let { " • $it" } ?: ""}",
                                                color = TextSecondary,
                                                fontFamily = Vazirmatn,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                pre.amount.money(),
                                                color = Success,
                                                fontFamily = Vazirmatn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                            Spacer(Modifier.height(3.dp))
                                            StatusChip(pre.type.label, Teal500)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrepaymentForm(
    customers: List<ir.atiran.hamrah.data.model.Customer>,
    onDismiss: () -> Unit,
    onSubmit: (ir.atiran.hamrah.data.model.Customer, Double, PrepayType, String?) -> Unit
) {
    var customerIndex by remember { mutableStateOf(0) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PrepayType.CASH) }

    val amount = amountText.trim().toLongOrNull() ?: 0L
    val customer = customers.getOrNull(customerIndex)

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "ثبت پیش‌دریافت جدید",
            color = TextPrimary,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Spacer(Modifier.height(12.dp))

        Text("مشتری:", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        // انتخاب مشتری — فهرست اسکرول‌شو
        val listScroll = rememberScrollState()
        Column(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .verticalScroll(listScroll)
        ) {
            customers.take(10).forEachIndexed { index, c ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (index == customerIndex) Teal500.copy(alpha = 0.18f) else Color(0x0FFFFFFF))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        c.name,
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontSize = 12.5.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (index == customerIndex) {
                        Icon(Icons.Rounded.Add, null, tint = Teal500, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassTextField(amountText, { v ->
            if (v.isBlank() || v.all { it.isDigit() }) amountText = v
        }, "مبلغ (تومان)")

        Spacer(Modifier.height(10.dp))
        GlassTextField(note, { note = it }, "توضیح (اختیاری)")

        Spacer(Modifier.height(12.dp))
        Text("نوع دریافتی:", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrepayType.values().forEach { t ->
                FilterPill(t.label, type == t) { type = t }
            }
        }

        Spacer(Modifier.height(18.dp))
        AtiranButton3D(
            text = "ثبت پیش‌دریافت",
            icon = Icons.Rounded.AccountBalanceWallet,
            style = Btn3DStyle.PRIMARY,
            enabled = customer != null && amount > 0,
            onClick = { customer?.let { onSubmit(it, amount.toDouble(), type, note.ifBlank { null }) } }
        )
        Spacer(Modifier.height(8.dp))
        AtiranButton3D(
            text = "انصراف",
            style = Btn3DStyle.GHOST,
            height = 46.dp,
            onClick = onDismiss
        )
        Spacer(Modifier.height(40.dp))
    }
}
