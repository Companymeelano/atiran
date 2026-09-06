package ir.atiran.hamrah.ui.screens.more

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.MenuGrid
import ir.atiran.hamrah.ui.components.SectionHeader
import ir.atiran.hamrah.ui.components.Tile3D
import ir.atiran.hamrah.ui.components.TileData
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * «منوی کامل» — همه بخش‌های برنامه به تفکیک گروه و با کاشی‌های سه‌بعدی.
 * این صفحه همان منوی سازمان‌یافته و خلاقانه است که خواسته اصلی پروژه بود.
 */
@Composable
fun MoreScreen(vm: MainViewModel, onOpen: (String) -> Unit) {
    val messages by vm.messages.collectAsState()
    val pending by vm.pending.collectAsState()
    val prefs by vm.prefs.collectAsState()
    val unread = messages.count { !it.read }

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
                .statusBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        "منوی کامل",
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    Text(
                        "همه امکانات آتیران همراه در یک نگاه",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .background(
                                Color(0x1A00C9B1),
                                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
                            )
                    )
                    Icon(
                        Icons.Rounded.ShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFF00C9B1),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            /* ---------- عملیات روزانه ---------- */
            SectionHeader(title = "عملیات روزانه")
            Spacer(Modifier.height(6.dp))
            MenuGrid(
                tiles = listOf(
                    TileData(
                        route = "new_invoice",
                        title = "فاکتور جدید",
                        subtitle = "ثبت فروش جدید",
                        icon = Icons.Rounded.ShoppingCart,
                        gradient = listOf(Color(0xFF8A5A00), Color(0xFF4A2E00)),
                        glow = Color(0xFFFFC94A)
                    ),
                    TileData(
                        route = "visits",
                        title = "مسیر و ویزیت",
                        subtitle = "چک‌این مشتریان امروز",
                        icon = Icons.Rounded.Route,
                        gradient = listOf(Color(0xFF0E5D4E), Color(0xFF083A31)),
                        glow = Color(0xFF37D6B5)
                    ),
                    TileData(
                        route = "prepayments",
                        title = "پیش‌دریافت",
                        subtitle = "ثبت دریافتی از مشتری",
                        icon = Icons.Rounded.AccountBalanceWallet,
                        gradient = listOf(Color(0xFF0F5132), Color(0xFF08301E)),
                        glow = Color(0xFF2ECC97)
                    ),
                    TileData(
                        route = "messages",
                        title = "پیام‌ها",
                        subtitle = "اعلان‌های سامانه",
                        icon = Icons.Rounded.Message,
                        gradient = listOf(Color(0xFF123A8A), Color(0xFF0A2255)),
                        glow = Color(0xFF5B8DEF),
                        badge = if (unread > 0) "${unread} جدید" else null
                    )
                ),
                onTile = onOpen
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- فروش و مالی ---------- */
            SectionHeader(title = "فروش و مالی")
            Spacer(Modifier.height(6.dp))
            MenuGrid(
                tiles = listOf(
                    TileData(
                        route = "invoices",
                        title = "فاکتورها و پیش‌فاکتورها",
                        subtitle = "گردش فروش",
                        icon = Icons.Rounded.ReceiptLong,
                        gradient = listOf(Color(0xFF1B4B9E), Color(0xFF0D2A5E)),
                        glow = Color(0xFF7FA9FF)
                    ),
                    TileData(
                        route = "checks",
                        title = "چک‌ها",
                        subtitle = "سررسید و وصول",
                        icon = Icons.Rounded.Payments,
                        gradient = listOf(Color(0xFF5C1F6B), Color(0xFF371242)),
                        glow = Color(0xFFB57BFF)
                    ),
                    TileData(
                        route = "reports",
                        title = "گزارش‌ها",
                        subtitle = "تحلیل عملکرد فروش",
                        icon = Icons.Rounded.Insights,
                        gradient = listOf(Color(0xFF8A2D5E), Color(0xFF521537)),
                        glow = Color(0xFFFF7EB3)
                    )
                ),
                onTile = onOpen
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- مدیریت ---------- */
            SectionHeader(title = "مدیریت")
            Spacer(Modifier.height(6.dp))
            MenuGrid(
                tiles = listOf(
                    TileData(
                        route = "customers",
                        title = "مشتریان",
                        subtitle = "پرونده و اعتبار",
                        icon = Icons.Rounded.Groups,
                        gradient = listOf(Color(0xFF00564C), Color(0xFF003832)),
                        glow = Color(0xFF00C9B1)
                    ),
                    TileData(
                        route = "products",
                        title = "کالا و انبار",
                        subtitle = "قیمت، موجودی، انبار",
                        icon = Icons.Rounded.Inventory2,
                        gradient = listOf(Color(0xFF7A4E00), Color(0xFF4A2E00)),
                        glow = Color(0xFFFFC94A)
                    )
                ),
                onTile = onOpen
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- سیستم ---------- */
            SectionHeader(title = "سیستم")
            Spacer(Modifier.height(6.dp))
            MenuGrid(
                tiles = listOf(
                    TileData(
                        route = "settings",
                        title = "تنظیمات",
                        subtitle = if (prefs.serverUrl.isBlank()) "حالت نمایشی فعال" else "متصل به سرور",
                        icon = Icons.Rounded.Settings,
                        gradient = listOf(Color(0xFF334064), Color(0xFF1D2542)),
                        glow = Color(0xFF9BA8CE),
                        badge = if (pending.isNotEmpty()) "${pending.size} در صف" else null
                    ),
                    TileData(
                        route = "about",
                        title = "درباره برنامه",
                        subtitle = "نسخه ۱.۰.۰",
                        icon = Icons.Rounded.Info,
                        gradient = listOf(Color(0xFF2C2C4E), Color(0xFF181830)),
                        glow = Color(0xFF9B8CFF)
                    )
                ),
                onTile = onOpen
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}
