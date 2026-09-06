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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GroupOff
import androidx.compose.material.icons.rounded.PersonAddAlt1
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.core.util.money
import ir.atiran.hamrah.data.model.Customer
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.AvatarCircle
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.SearchFieldGlass
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Success
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Warn
import ir.atiran.hamrah.ui.theme.Vazirmatn

private val FILTERS = listOf("همه", "بدهکار", "لیست سیاه", "فقط نقدی")

/**
 * فهرست مشتریان با جستجو، فیلتر و افزودن مشتری جدید
 */
@Composable
fun CustomersScreen(vm: MainViewModel, onOpenCustomer: (Int) -> Unit) {
    val customers by vm.customers.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("همه") }
    var showAdd by remember { mutableStateOf(false) }

    val filtered = customers.filter { c ->
        val matchQuery = query.isBlank() ||
                c.name.contains(query, ignoreCase = true) ||
                (c.code?.contains(query, ignoreCase = true) ?: false)
        val matchFilter = when (filter) {
            "بدهکار" -> c.man > 0
            "لیست سیاه" -> c.blackList
            "فقط نقدی" -> c.justNaghdi
            else -> true
        }
        matchQuery && matchFilter
    }

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
                    "مشتریان",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(8.dp))
                StatusChip("${customers.size} مشتری", Teal500)
                Spacer(Modifier.weight(1f))
                androidx.compose.material3.FilledIconButton(
                    onClick = { showAdd = true },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = Teal500,
                    contentColor = Color(0xFF00332D),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.Rounded.PersonAddAlt1, contentDescription = "مشتری جدید")
                }
            }

            Spacer(Modifier.height(14.dp))
            SearchFieldGlass(query, { query = it }, "جستجوی نام یا کد مشتری...")

            Spacer(Modifier.height(12.dp))

            // چیپ‌های فیلتر
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FILTERS.forEach { f ->
                    val selected = filter == f
                    Text(
                        text = f,
                        color = if (selected) Color(0xFF0A0F1E) else TextSecondary,
                        fontFamily = Vazirmatn,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Teal500 else Color(0x14FFFFFF))
                            .clickable { filter = f }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.GroupOff,
                    title = "مشتری‌ای یافت نشد",
                    subtitle = "عبارت جستجو یا فیلترها را تغییر دهید"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                ) {
                    items(filtered, key = { it.shmo }) { customer ->
                        CustomerRow(customer) { onOpenCustomer(customer.shmo) }
                    }
                }
            }
        }

        if (showAdd) {
            AddCustomerDialog(
                onDismiss = { showAdd = false },
                onConfirm = { name, group, cell, address ->
                    vm.addCustomer(name, group, cell, address)
                    showAdd = false
                }
            )
        }
    }
}

@Composable
private fun CustomerRow(customer: Customer, onClick: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(
                name = customer.name,
                sizeDp = 46,
                gradient = if (customer.blackList)
                    listOf(Color(0xFF8A2D2D), Color(0xFF4A1414))
                else
                    listOf(Color(0xFF00A38F), Color(0xFF00564C))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        customer.name,
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (customer.blackList) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Rounded.Warning, null, tint = Danger, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        customer.groupName ?: "بدون گروه",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 11.sp
                    )
                    if (customer.justNaghdi) {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("نقدی", Warn)
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    customer.man.money(),
                    color = if (customer.man > 0) Danger else Success,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(3.dp))
                StatusChip("اعتبار ${customer.cred.groupedShort()}", Teal500)
            }
        }
    }
}

/** نمایش فشرده مبلغ برای چیپ اعتبار */
private fun Double.groupedShort(): String {
    val millions = this / 1_000_000.0
    return if (millions >= 1.0) "${millions.toInt()}م" else "${(this / 1000).toInt()}ه"
}

@Composable
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, group: String, cell: String?, address: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cell by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("خرده‌فروشی") }
    val groups = listOf("زنجیره‌ای", "سوپرمارکت", "خرده‌فروشی", "صنایع غذایی", "کافه")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        GlassCard(Modifier.fillMaxWidth(), containerColor = Color(0xFF131C33)) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "ثبت مشتری جدید",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(14.dp))

                GlassTextField(name, { name = it }, "نام فروشگاه / مشتری")
                Spacer(Modifier.height(10.dp))
                GlassTextField(cell, { cell = it }, "شماره تماس")
                Spacer(Modifier.height(10.dp))
                GlassTextField(address, { address = it }, "آدرس")

                Spacer(Modifier.height(12.dp))
                Text("گروه مشتری:", color = TextSecondary, fontFamily = Vazirmatn, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    groups.take(3).forEach { g ->
                        FilterPill(g, group == g) { group = g }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    groups.drop(3).forEach { g ->
                        FilterPill(g, group == g) { group = g }
                    }
                }

                Spacer(Modifier.height(18.dp))
                AtiranButton3D(
                    text = "ثبت مشتری",
                    icon = Icons.Rounded.Add,
                    style = Btn3DStyle.PRIMARY,
                    enabled = name.isNotBlank(),
                    onClick = { onConfirm(name.trim(), group, cell.trim().ifBlank { null }, address.trim().ifBlank { null }) }
                )
            }
        }
    }
}

@Composable
fun GlassTextField(value: String, onValueChange: (String) -> Unit, hint: String) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(hint, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 13.sp) },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = Teal500,
            unfocusedBorderColor = Color(0x24FFFFFF),
            cursorColor = Teal500,
            focusedContainerColor = Color(0xFF0E1526),
            unfocusedContainerColor = Color(0xFF0E1526)
        )
    )
}

@Composable
fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Color(0xFF0A0F1E) else TextSecondary,
        fontFamily = Vazirmatn,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Teal500 else Color(0x14FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
