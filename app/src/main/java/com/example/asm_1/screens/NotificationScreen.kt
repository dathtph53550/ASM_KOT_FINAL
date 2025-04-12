package com.example.asm_1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asm_1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 🔼 Thanh App Bar
        TopAppBar(
            title = { Text("Notification", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            // 🔹 Hôm nay
            item {
                SectionTitle(title = "Today")
            }

            items(todayNotifications) { notification ->
                NotificationItem(notification)
            }

            // 🔹 Hôm qua
            item {
                SectionTitle(title = "Yesterday")
            }

            items(yesterdayNotifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

// 📌 Tiêu đề nhóm thông báo (Today, Yesterday)
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.Black,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

// 📌 Item thông báo
@Composable
fun NotificationItem(notification: NotificationData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Icon tròn
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(notification.iconBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = notification.icon),
                contentDescription = null,
                tint = notification.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 🔹 Nội dung thông báo
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }

    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
}

// 📌 Data class chứa thông tin thông báo
data class NotificationData(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val iconBgColor: Color,
    val iconTint: Color
)

// 📌 Danh sách thông báo hôm nay
val todayNotifications = listOf(
    NotificationData("30% Special Discount!", "Special promotion only valid today", R.drawable.discount, Color(0xFFFCE4EC), Color(0xFFD81B60)),
    NotificationData("Your Order Has Been Taken by the Driver", "Recently", R.drawable.ck, Color(0xFFE8F5E9), Color(0xFF4CAF50)),
    NotificationData("Your Order Has Been Canceled", "19 Jun 2023", R.drawable.cancel, Color(0xFFFFEBEE), Color(0xFFD32F2F))
)

// 📌 Danh sách thông báo hôm qua
val yesterdayNotifications = listOf(
    NotificationData("35% Special Discount!", "Special promotion only valid today", R.drawable.mail, Color(0xFFE3F2FD), Color(0xFF1E88E5)),
    NotificationData("Account Setup Successful!", "Special promotion only valid today", R.drawable.us, Color(0xFFF3E5F5), Color(0xFF8E24AA)),
    NotificationData("Special Offer! 60% Off", "Special offer for new account, valid until 20 Nov 2022", R.drawable.discount, Color(0xFFFCE4EC), Color(0xFFD81B60)),
    NotificationData("Credit Card Connected", "Special promotion only valid today", R.drawable.credit_card, Color(0xFFFFF3E0), Color(0xFFF57C00))
)
