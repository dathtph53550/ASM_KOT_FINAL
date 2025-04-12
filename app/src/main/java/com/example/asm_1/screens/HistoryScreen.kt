package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.Order
import com.example.asm_1.service.ViewModelApp
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Column {
                    Text(
                        "Order History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "${orders.size} Orders",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // Orders List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(orders.sortedByDescending { it.createAt }) { order ->
                OrderHistoryItem(order)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun OrderHistoryItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mã #${formatDate(order.createAt).hashCode().absoluteValue % 1000}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatDate(order.createAt),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            when (order.status) {
                                "pending" -> Color(0xFFFFB74D)
                                "confirmed" -> Color(0xFF81C784)
                                else -> Color(0xFFE57373)
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items
            order.order.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Check if the image is a URL or resource reference
                    if (item.image.startsWith("http")) {
                        // Load image from URL using AsyncImage
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        // For backward compatibility or default image
                        Image(
                            painter = painterResource(id = R.drawable.burger_1),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = item.price,
                            color = Color(0xFFFF9800),
                            fontSize = 14.sp
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Số Lượng",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                if (item != order.order.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray,
                        thickness = 0.5.dp
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.LightGray
            )

            // Delivery Info
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Người Nhận :",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = order.nameOrder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Địa Chỉ:",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = order.addressOrder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SĐT:",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = order.phoneOrder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val dateTime = OffsetDateTime.parse(dateString)
        dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
    } catch (e: Exception) {
        dateString
    }
}

fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } 