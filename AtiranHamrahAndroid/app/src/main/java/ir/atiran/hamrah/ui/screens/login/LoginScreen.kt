package ir.atiran.hamrah.ui.screens.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.R
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.AtiranButton3D
import ir.atiran.hamrah.ui.components.Btn3DStyle
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.theme.Gold400
import ir.atiran.hamrah.ui.theme.Space800
import ir.atiran.hamrah.ui.theme.Space950
import ir.atiran.hamrah.ui.theme.StrokeWhite
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * صفحه ورود ویزیتور — با فیلدهای شیشه‌ای و دکمه سه‌بعدی
 */
@Composable
fun LoginScreen(vm: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val prefs by vm.prefs.collectAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(Space950)
    ) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .imePadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            Box(
                Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(Color(0x2200C9B1)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                "آتیران همراه",
                color = TextPrimary,
                fontFamily = Vazirmatn,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "ورود ویزیتور به سامانه فروش",
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(30.dp))

            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp)) {
                    Text(
                        "ورود به حساب",
                        color = TextPrimary,
                        fontFamily = Vazirmatn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.height(18.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; error = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("نام کاربری", fontFamily = Vazirmatn, color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Rounded.Person, null, tint = Teal500) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Teal500,
                            unfocusedBorderColor = StrokeWhite,
                            cursorColor = Teal500,
                            focusedContainerColor = Space800,
                            unfocusedContainerColor = Space800
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("رمز عبور", fontFamily = Vazirmatn, color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = Teal500) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Teal500,
                            unfocusedBorderColor = StrokeWhite,
                            cursorColor = Teal500,
                            focusedContainerColor = Space800,
                            unfocusedContainerColor = Space800
                        )
                    )

                    if (error != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(error!!, color = Color(0xFFFF7A7A), fontFamily = Vazirmatn, fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(22.dp))

                    AtiranButton3D(
                        text = if (busy) "در حال بررسی..." else "ورود به سامانه",
                        icon = Icons.Rounded.Verified,
                        style = Btn3DStyle.PRIMARY,
                        enabled = !busy && username.isNotBlank() && password.isNotBlank(),
                        onClick = {
                            busy = true
                            vm.login(username, password) { ok ->
                                busy = false
                                if (!ok) error = "نام کاربری یا رمز عبور نادرست است"
                            }
                        }
                    )

                    Spacer(Modifier.height(14.dp))

                    AtiranButton3D(
                        text = "ورود نمایشی (بدون سرور)",
                        style = Btn3DStyle.GHOST,
                        onClick = {
                            busy = true
                            vm.login("demo", "demo") { ok ->
                                busy = false
                                if (!ok) error = "ورود نمایشی با خطا مواجه شد"
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(
                    text = if (prefs.serverUrl.isBlank()) "حالت نمایشی فعال" else "سرور: ${prefs.serverUrl}",
                    color = if (prefs.serverUrl.isBlank()) Gold400 else Teal500
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}
