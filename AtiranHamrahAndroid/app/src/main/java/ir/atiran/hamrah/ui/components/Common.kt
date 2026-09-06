package ir.atiran.hamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.ui.theme.Space800
import ir.atiran.hamrah.ui.theme.StrokeWhite
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn

/**
 * نوار بالای برنامه (شیشه‌ای) با دکمه بازگشت و عملیات دلخواه
 */
@Composable
fun AppBarGlass(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            GlassIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "بازگشت",
                onClick = onBack
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = title,
            color = TextPrimary,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}

/**
 * فیلد جستجوی شیشه‌ای
 */
@Composable
fun SearchFieldGlass(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(hint, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = Teal500,
            unfocusedBorderColor = StrokeWhite,
            cursorColor = Teal500,
            focusedContainerColor = Space800,
            unfocusedContainerColor = Space800,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary
        )
    )
}

/**
 * حالت خالی (بدون داده)
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 42.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color(0x14FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(
            title,
            color = TextPrimary,
            fontFamily = Vazirmatn,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                subtitle,
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 12.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** چراغ بارگذاری ساده */
@Composable
fun LoadingBox(message: String = "در حال دریافت اطلاعات...") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Teal500,
            strokeWidth = 3.dp,
            modifier = Modifier.size(38.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(message, color = TextSecondary, fontFamily = Vazirmatn, fontSize = 13.sp)
    }
}
