package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.asm_1.service.ViewModelApp
import com.example.asm_1.models.Order
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton

data class UserProfile(
    val name: String,
    val email: String,
    val avatarRes: Int
)

val user = UserProfile(
    name = "Albert Stevano Bajefski",
    email = "Albertstevano@gmail.com",
    avatarRes = R.drawable.avatar
)

// Add necessary imports


@Composable
fun ProfileScreen(navController: NavHostController) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    // Store context at the composable level
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Extra padding for bottom navigation
        ) {
            ProfileHeader(user)
            OrderSection(navController)
            AccountOptions(navController)
            Spacer(modifier = Modifier.height(24.dp))
            SignOutButton { showSignOutDialog = true }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Sign Out Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Sign Out",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Text(
                    "Do you want to log out?",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Use the context captured from the composable
                        val intent = android.content.Intent(context, com.example.asm_1.MainActivity::class.java)
                        // Add flags to clear the back stack
                        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                        // Add extra to indicate we want to show the login screen
                        intent.putExtra("SHOW_LOGIN", true)
                        // Start the activity
                        context.startActivity(intent)
                        showSignOutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "Log Out",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "Cancel",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(user: UserProfilee) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painterResource(id = user.avatarRes),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Change Profile Picture",
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.White, shape = CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(user.email, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun OrderSection(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    val orders by viewModel.orders.collectAsState()
    
    // Fetch orders when the component is first created
    LaunchedEffect(Unit) {
        viewModel.getOrders()
    }
    
    // Get the most recent order
    val mostRecentOrder = orders.maxByOrNull { it.createAt ?: "" }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("My Orders", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Xem tất cả",
                    color = Color(0xFFFF9800),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("History")
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (mostRecentOrder != null && mostRecentOrder.order.isNotEmpty()) {
                Column {
                    // Hiển thị tất cả sản phẩm trong đơn hàng
                    mostRecentOrder.order.forEach { item ->
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
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                // For backward compatibility or default image
                                Image(
                                    painter = painterResource(id = R.drawable.burger_1),
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text(item.price, color = Color(0xFFFF9800))
                                Text("Quantity: ${item.quantity}", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        if (item != mostRecentOrder.order.last()) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Hiển thị trạng thái đơn hàng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${mostRecentOrder.order.size} items",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    when (mostRecentOrder.status) {
                                        "pending" -> Color(0xFFFFB74D)
                                        "confirmed" -> Color(0xFF81C784)
                                        else -> Color(0xFFE57373)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                mostRecentOrder.status?.replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                } ?: "Unknown",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    "No orders yet",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AccountOptions(navController: NavHostController) {
    val options = listOf(
        "Personal Data" to Icons.Default.Person,
        "Settings" to Icons.Default.Settings,
    )
    val supportOptions = listOf(
        "Help Center" to Icons.Default.Info,
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        options.forEach { (title, icon) -> AccountOptionItem(title, icon, navController) }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Support", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        supportOptions.forEach { (title, icon) -> AccountOptionItem(title, icon, navController) }
    }
}

@Composable
fun AccountOptionItem(title: String, icon: ImageVector, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                if(title == "Personal Data"){
                    navController.navigate("Profile")
                }
                else if(title == "Settings"){
                    navController.navigate("Setting")
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Text(title, modifier = Modifier.padding(start = 16.dp), fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
    }
}

@Composable
fun SignOutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.ExitToApp, contentDescription = "Đăng Xuất", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Đăng Xuất", color = Color.White, fontSize = 16.sp)
    }
}



