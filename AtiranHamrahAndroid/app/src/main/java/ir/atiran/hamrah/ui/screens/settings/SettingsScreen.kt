package ir.atiran.hamrah.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AppBarGlass
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.GlassTextField
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * تنظیمات: نشانی سرور WCF، حالت نمایشی، ظاهر و خروج
 */
@Composable
fun SettingsScreen(vm: MainViewModel, onOpenAbout: () -> Unit) {
    val prefs by vm.prefs.collectAsState()
    val pending by vm.pending.collectAsState()
    var urlText by remember(prefs.serverUrl) { mutableStateOf(prefs.serverUrl) }

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
                .padding(horizontal = 18.dp)
        ) {
            AppBarGlass(title = "تنظیمات")

            /* ---------- اتصال به سرور ---------- */
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Dns, null, tint = Teal500, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "اتصال به سرور آتیران (WCF)",
                            color = TextPrimary,
                            fontFamily = Vazirmatn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.weight(1f))
                        StatusChip(
                            if (prefs.serverUrl.isBlank()) "نمایشی" else "تنظیم‌شده",
                            if (prefs.serverUrl.isBlank()) Gold400 else Teal500
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "نشانی سرویس LocalServices را وارد کنید؛ مثال: http://192.168.1.10/AtiranLocalServices/LocalServices.svc",
                        color = TextSecondary,
                        fontFamily = Vazirmatn,
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    GlassTextField(urlText, { urlText = it }, "http://...")

                    Spacer(Modifier.height(10.dp))
                    AtiranButton3D(
                        text = "ذخیره و بررسی اتصال",
                        icon = Icons.Rounded.CloudSync,
                        style = Btn3DStyle.PRIMARY,
                        height = 48.dp,
                        enabled = true,
                        onClick = { vm.setServerUrl(urlText.trim()) }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- حالت نمایشی ---------- */
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "حالت نمایشی (بدون سرور)",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "با داده نمونه کار می‌کنید؛ ثبت‌ها به صف همگام‌سازی می‌روند",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = prefs.serverUrl.isBlank(),
                            onCheckedChange = { checked ->
                                if (!checked) {
                                    // روشن‌کردن حالت سرور نیازمند نشانی است
                                } else {
                                    vm.setServerUrl("")
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = Teal500,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- ظاهر ---------- */
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Palette, null, tint = Gold400, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "تم تیره",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                "پس‌زمینه فضایی با رنگ فیروزه‌ای و طلایی",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = prefs.darkTheme,
                            onCheckedChange = { vm.setDarkTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = Teal500,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- همگام‌سازی ---------- */
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Sync, null, tint = Teal500, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "صف همگام‌سازی",
                                color = TextPrimary,
                                fontFamily = Vazirmatn,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                if (pending.isEmpty()) "خالی — همه‌چیز ارسال شده"
                                else "${pending.size} عملیات در انتظار ارسال",
                                color = TextSecondary,
                                fontFamily = Vazirmatn,
                                fontSize = 11.sp
                            )
                        }
                        StatusChip(
                            if (pending.isEmpty()) "پاک" else "${pending.size}",
                            if (pending.isEmpty()) Teal500 else Gold400
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- درباره ---------- */
            GlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Info, null, tint = Teal500, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "درباره آتیران همراه",
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "مشاهده",
                        color = Teal500,
                        fontFamily = Vazirmatn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenAbout)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            AtiranButton3D(
                text = "خروج از حساب کاربری",
                icon = Icons.Rounded.Logout,
                style = Btn3DStyle.DANGER,
                height = 50.dp,
                onClick = { vm.logout() }
            )

            Spacer(Modifier.height(120.dp))
        }
    }
}
