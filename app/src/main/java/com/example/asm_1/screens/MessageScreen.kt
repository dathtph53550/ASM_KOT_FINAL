package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asm_1.R

data class Chat(
    val id: Int,
    val senderName: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val isRead: Boolean = false,
    val avatarRes: Int
)

val sampleChats = listOf(
    Chat(1, "Geopart Etdsien", "Your Order Just Arrived!", "13.47", 0, true, R.drawable.avatar_1),
    Chat(2, "Stevano Clirover", "Your Order Just Arrived!", "11.23", 3, false, R.drawable.avatar_2),
    Chat(3, "Elisia Justin", "Your Order Just Arrived!", "11.23", 0, true, R.drawable.avatar_3),
    Chat(4, "Geopart Etdsien", "Your Order Just Arrived!", "13.47", 0, true, R.drawable.avatar_1),
    Chat(5, "Stevano Clirover", "Your Order Just Arrived!", "11.23", 3, false, R.drawable.avatar_2),
    Chat(6, "Elisia Justin", "Your Order Just Arrived!", "11.23", 0, true, R.drawable.avatar_3)
)


@Composable
fun MessageScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        ChatListTopBar()
        LazyColumn {
            items(sampleChats) { chat ->
                ChatItem(chat, navController = navController)
            }
        }
    }
}

@Composable
fun ChatListTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Chat List",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "All Message",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )
    }
}

@Composable
fun ChatItem(chat: Chat,navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable {
                if(chat.senderName == "Geopart Etdsien"){
                    navController.navigate("Chat")
                }
            }
            .padding(12.dp)
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = chat.avatarRes),
            contentDescription = chat.senderName,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(chat.senderName, fontWeight = FontWeight.Bold)
            Text(chat.lastMessage, color = Color.Gray, fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(chat.time, fontSize = 12.sp, color = Color.Gray)
            if (chat.unreadCount > 0) {
                Badge(chat.unreadCount)
            } else if (chat.isRead) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Read",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


@Composable
fun Badge(count: Int) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(Color(0xFFFF9800), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}




