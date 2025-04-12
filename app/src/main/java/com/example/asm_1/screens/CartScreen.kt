package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.CartItem
import com.example.asm_1.service.ViewModelApp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    // Fetch cart items when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.getCartItems()
    }
    
    val cartItems = viewModel.listCart.value ?: emptyList()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi giỏ hàng?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCart()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        CartTopBar(navController, viewModel, onClearCartClick = { showDeleteConfirmation = true })
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                CartItemRow(item, viewModel)
            }
            item {
                RecommendedSection()
                PaymentSummary(cartItems)
            }
        }
        OrderNowButton(navController, cartItems)
    }
}


@Composable
fun CartTopBar(
    navController: NavHostController, 
    viewModel: ViewModelApp = viewModel(),
    onClearCartClick: () -> Unit
) {
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
            text = "My Cart",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun CartItemRow(item: CartItem, viewModel: ViewModelApp) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm \"${item.name}\" khỏi giỏ hàng?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFromCart(item.id!!)
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
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
                // For backward compatibility with resource IDs stored as strings
                Image(
                    painter = painterResource(id = R.drawable.burger_1),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(item.price + " đ", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
            }
            QuantityControl(item, viewModel)
            IconButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.background(Color(0xFFFFEEEE), RoundedCornerShape(4.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, 
                    contentDescription = "Remove", 
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun QuantityControl(item: CartItem, viewModel: ViewModelApp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { 
            if (item.quantity > 1) {
                // Tạo sản phẩm mới với số lượng giảm đi 1
                val updatedItem = CartItem(
                    id = item.id,
                    name = item.name,
                    image = item.image,
                    price = item.price,
                    quantity = item.quantity - 1, // Số lượng giảm đi 1
                    isChecked = item.isChecked
                )
                
                // Cập nhật sản phẩm trong giỏ hàng
                viewModel.updateCartItem(item.id!!, updatedItem)
            }
        }) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
        }
        Text(text = item.quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { 
            // Tạo sản phẩm mới với số lượng tăng thêm 1
            val updatedItem = CartItem(
                id = item.id,
                name = item.name,
                image = item.image,
                price = item.price,
                quantity = item.quantity + 1, // Số lượng tăng thêm 1
                isChecked = item.isChecked
            )
            
            // Cập nhật sản phẩm trong giỏ hàng
            viewModel.updateCartItem(item.id!!, updatedItem)
        }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
        }
    }
}

@Composable
fun RecommendedSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recommended For You", fontWeight = FontWeight.Bold)
            Text("See All", color = Color(0xFFFF9800))
        }
        // We'll keep this section for now, but it should be updated to use actual product data
        LazyRow {
            items(3) { index ->
                RecommendedItem(index)
            }
        }
    }
}

@Composable
fun RecommendedItem(index: Int) {
    // Sample image URLs, could be replaced with actual remote URLs
    val imageUrl = when(index) {
        0 -> "https://i.pinimg.com/474x/85/9a/de/859adef90e854238d9b330d0c7d2cf73.jpg"
        1 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRft9F9rvyCZ4eZMSFaJKQdrbC2KsUPnVi2IQ&usqp=CAU"
        else -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGdNHAVfPr8BzU1xnZvNAsuGg8zgAIZp-0dQ&usqp=CAU"
    }
    
    val name = when(index) {
        0 -> "Burger With Meat"
        1 -> "Ordinary Burgers"
        else -> "Special Burger"
    }
    
    val price = when(index) {
        0 -> "$17,230"
        1 -> "$15,000"
        else -> "$20,000"
    }
    
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Load image from URL using AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(price, color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
            }
        }
    }
}



@Composable
fun PaymentSummary(cartItems: List<CartItem>) {
    val totalItems = cartItems.sumOf { it.quantity }
    val totalPrice = cartItems.sumOf {
        val numericPrice = it.price
            .replace(".", "") // Xoá dấu phân cách hàng nghìn
            .toDoubleOrNull() ?: 0.0
        numericPrice * it.quantity
    }
    

    // Format the total price with periods as thousands separators
    val formattedTotalPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(totalPrice)


    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tổng tiền thanh toán", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng số lượng ($totalItems)")
                Text("$formattedTotalPrice đ", fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng Tiền", fontWeight = FontWeight.Bold)
                Text("$formattedTotalPrice đ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun OrderNowButton(navController: NavHostController, cartItems: List<CartItem>) {
    Button(
        onClick = { 
            // Điều hướng đến PaymentScreen
            if (cartItems.isNotEmpty()) {
                navController.navigate("Payment") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
    ) {
        Text("Đặt Hàng Ngay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
