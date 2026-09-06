package ir.atiran.hamrah.ui.screens.messages

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material.icons.rounded.NotificationsActive
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.atiran.hamrah.data.model.Message
import ir.atiran.hamrah.data.model.MessagePriority
import ir.atiran.hamrah.ui.MainViewModel
import ir.atiran.hamrah.ui.components.AuroraBackground
import ir.atiran.hamrah.ui.components.EmptyState
import ir.atiran.hamrah.ui.components.GlassCard
import ir.atiran.hamrah.ui.components.StatusChip
import ir.atiran.hamrah.ui.screens.customers.FilterPill
import ir.atiran.hamrah.ui.theme.Danger
import ir.atiran.hamrah.ui.theme.Space900
import ir.atiran.hamrah.ui.theme.Teal500
import ir.atiran.hamrah.ui.theme.TextPrimary
import ir.atiran.hamrah.ui.theme.TextSecondary
import ir.atiran.hamrah.ui.theme.Vazirmatn
import ir.atiran.hamrah.ui.theme.Warn

/**
 * صندوق پیام‌های سامانه (معادل VisitorMessages در سرویس WCF)
 */
@Composable
fun MessagesScreen(vm: MainViewModel) {
    val messages by vm.messages.collectAsState()
    var filter by remember { mutableStateOf("همه") }
    var expanded by remember { mutableStateOf<Long?>(null) }

    val filtered = messages.filter {
        when (filter) {
            "خوانده‌نشده" -> !it.read
            else -> true
        }
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
                    "پیام‌ها",
                    color = TextPrimary,
                    fontFamily = Vazirmatn,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(8.dp))
                val unread = messages.count { !it.read }
                if (unread > 0) StatusChip("$unread خوانده‌نشده", Danger)
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill("همه", filter == "همه") { filter = "همه" }
                FilterPill("خوانده‌نشده", filter == "خوانده‌نشده") { filter = "خوانده‌نشده" }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Email,
                    title = "پیامی وجود ندارد",
                    subtitle = "پیام‌های جدید مدیران و سامانه اینجا نمایش داده می‌شود"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 130.dp)
                ) {
                    items(filtered, key = { it.id }) { message ->
                        MessageCard(
                            message = message,
                            expanded = expanded == message.id,
                            onToggle = {
                                expanded = if (expanded == message.id) null else message.id
                                if (!message.read) vm.markMessageRead(message.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageCard(message: Message, expanded: Boolean, onToggle: () -> Unit) {
    val priorityColor = when (message.priority) {
        MessagePriority.NORMAL -> Teal500
        MessagePriority.HIGH -> Warn
        MessagePriority.URGENT -> Danger
    }
    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // نقطه خوانده‌نشده
                Box(
                    Modifier
                        .size(if (message.read) 7.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (message.read) Color(0x33FFFFFF) else priorityColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    message.title,
                    color = if (message.read) TextPrimary else Color(0xFFEAF0FF),
                    fontFamily = Vazirmatn,
                    fontWeight = if (message.read) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 13.5.sp,
                    modifier = Modifier.weight(1f)
                )
                if (message.priority != MessagePriority.NORMAL) {
                    StatusChip(message.priority.label, priorityColor)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                message.body,
                color = TextSecondary,
                fontFamily = Vazirmatn,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (message.read) Icons.Rounded.MarkEmailRead else Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    tint = if (message.read) TextSecondary else Teal500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    buildString {
                        append(message.sender ?: "سامانه")
                        append(" • ")
                        append(message.date)
                        message.time?.let { append(" ساعت $it") }
                    },
                    color = TextSecondary,
                    fontFamily = Vazirmatn,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (expanded) "بستن" else "مشاهده کامل",
                    color = Teal500,
                    fontFamily = Vazirmatn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onToggle() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
