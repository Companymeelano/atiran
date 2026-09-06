package ir.atiran.hamrah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import ir.atiran.hamrah.ui.AppRoot
import ir.atiran.hamrah.ui.theme.HamrahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // چیدمان کامل راست‌به‌چپ برای فارسی
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HamrahTheme {
                    AppRoot()
                }
            }
        }
    }
}
