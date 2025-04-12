package com.example.asm_1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

data class Message(
    val text: String,
    val time: String,
    val isSent: Boolean // true: người dùng gửi, false: tin nhắn nhận được
)

// Dữ liệu mẫu
val messages = listOf(
    Message("Just to order", "09:00", isSent = false),
    Message("Okay, for what level of spiciness?", "09:15", isSent = true),
    Message("Okay, Wait a minute 🙏", "09:00", isSent = false),
    Message("Okay, I'm waiting 🙌", "09:15", isSent = true)
)


@Composable
fun ChatScreen(navController: NavHostController) {
    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Thanh tiêu đề
        ChatHeader(navController)

        // Danh sách tin nhắn
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        // Ô nhập tin nhắn
        ChatInputField(
            messageText = messageText,
            onMessageChange = { messageText = it },
            onSend = { /* TODO: Xử lý gửi tin nhắn */ }
        )
    }
}

@Composable
fun ChatHeader(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "Stevano Clirover",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { /* TODO: Xử lý gọi điện */ }) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
        }
    }
}


@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isSent) Arrangement.End else Arrangement.Start
    ) {
        Column {
            if (!message.isSent) {
                Text(
                    text = "Stevano Clirover",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = if (message.isSent) Color(0xFFFF9800) else Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(text = message.text, color = if (message.isSent) Color.White else Color.Black)
            }
            Text(
                text = message.time,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ChatInputField(messageText: String, onMessageChange: (String) -> Unit, onSend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Type something...") },
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSend,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(50)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

