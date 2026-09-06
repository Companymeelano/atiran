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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ir.atiran.hamrah.data.model.Customer
import ir.atiran.hamrah.data.model.Kala
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.SearchFieldGlass
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.screens.customers.GlassTextField
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space800
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.StrokeWhite
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Warn
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * ثبت فاکتور/پیش‌فاکتور جدید:
 * انتخاب مشتری ← افزودن کالا ← تخفیف ← ثبت نهایی
 */
@Composable
fun NewInvoiceScreen(
    vm: MainViewModel,
    presetShmo: Int,
    onClose: () -> Unit,
    onOpenInvoice: (Long) -> Unit
) {
    val customers by vm.customers.collectAsState()
    val kalas by vm.kalas.collectAsState()
    val cart by vm.cart.collectAsState()
    val cartCustomer by vm.cartCustomer.collectAsState()

    var step by remember { mutableStateOf(0) } // 0: مشتری، 1: کالاها، 2: پرداخت
    var query by remember { mutableStateOf("") }
    var discountText by remember { mutableStateOf("") }
    var isPre by remember { mutableStateOf(false) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    // مشتری از مسیر پرونده مشتری از قبل انتخاب شده باشد
    LaunchedEffect(presetShmo, customers) {
        if (presetShmo > 0 && vm.cartCustomer.value == null) {
            customers.firstOrNull { it.shmo == presetShmo }?.let {
                vm.setCartCustomer(it)
                step = 1
            }
        }
    }

    val discount = discountText.trim().toDoubleOrNull() ?: 0.0
    val sum = cart.sumOf { it.lineSum }
    val afterDiscount = (sum - discount).coerceAtLeast(0.0)
    val tax = afterDiscount * 0.10
    val avarez = tax * 0.25
    val total = afterDiscount + tax + avarez

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        AuroraBackground()

        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            AppBarGlass(
                title = if (isPre) "ثبت پیش‌فاکتور" else "ثبت فاکتور جدید",
                onBack = onClose,
                actions = {
                    // کلید نوع سند
                    Text(
                        if (isPre) "پیش‌فاکتور" else "فاکتور قطعی",
                        color = Color(0xFF0A0F1E),
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Gold400)
                            .clickable { isPre = !isPre }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            )

            /* ---------- نشانگر مراحل ---------- */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepDot("۱. مشتری", step >= 0, done = step > 0, Modifier.weight(1f))
                StepLine(step > 0)
                StepDot("۲. اقلام", step >= 1, done = step > 1, Modifier.weight(1f))
                StepLine(step > 1)
                StepDot("۳. ثبت", step >= 2, done = false, Modifier.weight(1f))
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(Modifier.height(14.dp))

                when (step) {
                    0 -> {
                        /* ---- مرحله ۱: انتخاب مشتری ---- */
                        if (!showCustomerPicker) {
                            Text(
                                "مشتری این فاکتور کیست؟",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            customers.take(6).forEach { customer ->
                                CustomerPickRow(customer) {
                                    vm.setCartCustomer(it)
                                    step = 1
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            AtiranButton3D(
                                text = "جستجوی بین همه مشتریان",
                                icon = Icons.Rounded.PersonSearch,
                                style = Btn3DStyle.GHOST,
                                onClick = { showCustomerPicker = true }
                            )
                        } else {
                            SearchFieldGlass(query, { query = it }, "نام مشتری...")
                            Spacer(Modifier.height(10.dp))
                            val filtered = customers.filter {
                                query.isBlank() || it.name.contains(query, true)
                            }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(320.dp)
                            ) {
                                items(filtered, key = { it.shmo }) { customer ->
                                    CustomerPickRow(customer) {
                                        vm.setCartCustomer(it)
                                        showCustomerPicker = false
                                        step = 1
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        /* ---- مرحله ۲: اقلام ---- */
                        cartCustomer?.let { customer ->
                            GlassCard(Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Storefront, null, tint = Gold400, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            customer.name,
                                            color = TextPrimary,
                                            fontFamily = Vazirmatn,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            "اعتبار: ${customer.cred.money()} • مانده: ${customer.man.money()}",
                                            color = TextSecondary,
                                            fontFamily = Vazirmatn,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "تغییر مشتری",
                                        tint = Danger,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                vm.setCartCustomer(null)
                                                step = 0
                                            }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        SearchFieldGlass(query, { query = it }, "جستجوی کالا برای افزودن...")

                        Spacer(Modifier.height(10.dp))

                        val filteredKalas = kalas.filter {
                            query.isBlank() || it.name.contains(query, true) || it.code.contains(query, true)
                        }

                        if (filteredKalas.isEmpty()) {
                            EmptyState(
                                icon = Icons.Rounded.ShoppingCart,
                                title = "کالایی یافت نشد",
                                subtitle = "نام یا کد کالای دیگری را جستجو کنید"
                            )
                        } else {
                            filteredKalas.take(8).forEach { kala ->
                                KalaPickRow(kala) { vm.addToCart(kala) }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        /* ---- سبد خرید ---- */
                        if (cart.isNotEmpty()) {
                            Text(
                                "سبد سفارش (${cart.size.toPersianDigits()} کالا)",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            cart.forEach { line ->
                                CartLineRow(
                                    name = line.kala.name,
                                    unit = line.kala.unit,
                                    qty = line.qty,
                                    price = line.kala.price,
                                    lineSum = line.lineSum,
                                    onAdd = { vm.setCartQty(line.kala, line.qty + 1) },
                                    onRemove = {
                                        if (line.qty <= 1.0) vm.removeFromCart(line.kala)
                                        else vm.setCartQty(line.kala, line.qty - 1)
                                    },
                                    onDelete = { vm.removeFromCart(line.kala) }
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            AtiranButton3D(
                                text = "ادامه و محاسبه مالیات",
                                icon = Icons.Rounded.RequestQuote,
                                style = Btn3DStyle.PRIMARY,
                                onClick = { step = 2 }
                            )
                        }
                    }

                    2 -> {
                        /* ---- مرحله ۳: جمع‌بندی و ثبت ---- */
                        GlassCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "جمع‌بندی مالی",
                                    color = TextPrimary,
                                    fontFamily = Vazirmatn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                SummaryRow("جمع اقلام", sum.money())
                                SummaryRow("تخفیف", discount.money(), Warn)
                                SummaryRow("مالیات (۱۰٪)", tax.money())
                                SummaryRow("عوارض (۲۵٪ مالیات)", avarez.money())
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(StrokeWhite)
                                )
                                Spacer(Modifier.height(8.dp))
                                SummaryRow("قابل پرداخت", total.money(), Teal500, big = true)
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        GlassTextField(discountText, { v ->
                            if (v.isBlank() || v.all { it.isDigit() }) discountText = v
                        }, "مبلغ تخفیف (تومان — اختیاری)")

                        Spacer(Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterPill("فاکتور قطعی", !isPre) { isPre = false }
                            FilterPill("پیش‌فاکتور", isPre) { isPre = true }
                        }

                        Spacer(Modifier.height(18.dp))
                        AtiranButton3D(
                            text = if (isPre) "ثبت پیش‌فاکتور نهایی" else "ثبت فاکتور نهایی",
                            icon = Icons.Rounded.ShoppingCart,
                            style = Btn3DStyle.GOLD,
                            enabled = cart.isNotEmpty() && cartCustomer != null,
                            onClick = {
                                vm.submitCart(discount, isPre) { shfacfo ->
                                    onOpenInvoice(shfacfo)
                                }
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        AtiranButton3D(
                            text = "بازگشت به اقلام",
                            style = Btn3DStyle.GHOST,
                            height = 48.dp,
                            onClick = { step = 1 }
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun StepDot(label: String, active: Boolean, done: Boolean, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    when {
                        done -> Success
                        active -> Teal500
                        else -> Color(0x33FFFFFF)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(Icons.Rounded.Check, null, tint = Color(0xFF062A22), modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (active) TextPrimary else TextSecondary,
            fontFamily = Vazirmatn,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun StepLine(active: Boolean) {
    Box(
        Modifier
            .width(18.dp)
            .height(2.dp)
            .background(if (active) Teal500 else Color(0x33FFFFFF))
    )
}

@Composable
private fun CustomerPickRow(customer: Customer, onClick: (Customer) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick(customer) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Groups, null, tint = Teal500, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    customer.name,
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp
                )
                Text(
                    "${customer.groupName ?: "—"} • کد ${customer.code ?: "—"}",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 10.5.sp
                )
            }
            StatusChip("مانده ${customer.man.money()}", if (customer.man > 0) Danger else Success)
        }
    }
}

@Composable
private fun KalaPickRow(kala: Kala, onAdd: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Row {
                    Text(
                        kala.groupName,
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "موجودی: ${kala.stockVah.toPersianDigits()} ${kala.unit}",
                        color = if (kala.stockVah < kala.reorderPoint) Danger else Success,
                        fontFamily = Vazirmatn,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                kala.price.money(),
                color = Gold400,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(Color(0xFF00A38F), Color(0xFF007D6E))))
                    .clickable(onClick = onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "افزودن", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun CartLineRow(
    name: String,
    unit: String,
    qty: Double,
    price: Double,
    lineSum: Double,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    name,
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "حذف",
                    tint = Danger,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete)
                        .padding(2.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // دکمه‌های کم و زیاد
                Row(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Space800),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = "زیاد",
                        tint = Teal500,
                        modifier = Modifier
                            .clickable(onClick = onAdd)
                            .padding(6.dp)
                            .size(16.dp)
                    )
                    Text(
                        qty.toPersianDigits(),
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Icon(
                        Icons.Rounded.Remove,
                        contentDescription = "کم",
                        tint = Warn,
                        modifier = Modifier
                            .clickable(onClick = onRemove)
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "$unit × ${price.money()}",
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 10.5.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    lineSum.money(),
                    color = Teal500,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, tint: Color, big: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = TextSecondary,
            fontFamily = Vazirmatn,
            fontSize = if (big) 13.sp else 12.5.sp
        )
        Spacer(Modifier.weight(1f))
        Text(
            value,
            color = tint,
            fontFamily = Vazirmatn,
            fontWeight = if (big) FontWeight.Black else FontWeight.SemiBold,
            fontSize = if (big) 17.sp else 12.5.sp
        )
    }
}
