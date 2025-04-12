package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.CartItem
import com.example.asm_1.service.ViewModelApp


@Composable
fun PaymentScreen(navController: NavHostController, backStackEntry: NavBackStackEntry, viewModel: ViewModelApp = viewModel()) {
    // Lấy dữ liệu giỏ hàng từ ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.getCartItems()
    }
    
    val cartItems = viewModel.listCart.value ?: emptyList()
    
    // State cho các trường nhập liệu
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    // Tính tổng tiền
    val totalPrice = cartItems.sumOf { 
        it.quantity * (it.price.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Nút Back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Payment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))


        Spacer(modifier = Modifier.height(16.dp))

        // Thông tin sản phẩm
        Text(text = "Items Ordered (${cartItems.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        
        // Hiển thị danh sách sản phẩm
        cartItems.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    // For backward compatibility with resource IDs stored as strings
                    Image(
                        painter = painterResource(id = R.drawable.burger_1),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = item.price, color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                    Text(text = "Quantity: ${item.quantity}", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tổng tiền
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tổng Tiền", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "$ ${String.format("%,.3f", totalPrice)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Thông tin giao hàng
        Text(text = "Giap hàng đến :", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Các trường nhập liệu
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên người nhận") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("SĐT") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Địa Chỉ") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Nút Checkout
        Button(
            onClick = { 
                // Gửi đơn hàng lên server
                viewModel.sendOrder(name, address, phone)
                // Điều hướng đến SuccessScreen
                navController.navigate("Success") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "Thanh Toán Ngay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Hàm phân tích dữ liệu giỏ hàng từ chuỗi
fun parseCartData(cartData: String): List<CartItem> {
    if (cartData.isEmpty()) return emptyList()
    
    println("Received cart data: $cartData")
    
    return cartData.split("|").mapNotNull { itemData ->
        try {
            // Xử lý các ký tự đặc biệt đã được mã hóa
            val parts = itemData.split(",").toMutableList()
            
            // Nếu có ít nhất 5 phần, tiếp tục xử lý
            if (parts.size >= 5) {
                val id = parts[0]
                val name = parts[1].replace("\\,", ",").replace("\\|", "|")
                val image = parts[2]
                val price = parts[3].replace("\\,", ",").replace("\\|", "|")
                val quantity = parts[4].toInt()
                
                println("Parsed item: id=$id, name=$name, image=$image, price=$price, quantity=$quantity")
                
                CartItem(
                    id = null,
                    name = name,
                    image = image,
                    price = price,
                    quantity = quantity,
                    isChecked = false
                )
            } else {
                println("Invalid cart item format (not enough parts): $itemData")
                null
            }
        } catch (e: Exception) {
            println("Error parsing cart item: $e")
            null
        }
    }
}

@Composable
fun PaymentRow(label: String, price: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(
            text = "$ ${String.format("%,.3f", price)}",
            fontSize = 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) Color(0xFFFF9800) else Color.Black
        )
    }
}


