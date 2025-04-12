package com.example.asm_1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(navController: NavHostController) {
    var isNotificationEnabled by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Nút quay lại + Tiêu đề
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Profile
        Text("PROFILE", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingToggleItem(
            title = "Push Notification",
            isChecked = isNotificationEnabled,
            onCheckedChange = { isNotificationEnabled = it }
        )
        SettingToggleItem(
            title = "Location",
            isChecked = isLocationEnabled,
            onCheckedChange = { isLocationEnabled = it }
        )

        SettingItem(title = "Language", value = "English", onClick = { /* TODO: Handle click */ })

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Other
        Text("OTHER", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingItem(title = "About Ticketis", onClick = { /* TODO: Handle click */ })
        SettingItem(title = "Privacy Policy", onClick = { /* TODO: Handle click */ })
        SettingItem(title = "Terms and Conditions", onClick = { /* TODO: Handle click */ })
    }
}

@Composable
fun SettingItem(title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            value?.let {
                Text(it, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun SettingToggleItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFF9800) // Màu cam giống ảnh
            )
        )
    }
}




