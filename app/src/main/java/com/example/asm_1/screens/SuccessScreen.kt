package com.example.asm_1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asm_1.R
import com.example.asm_1.service.ViewModelApp
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    // Animation scale
    val scale = remember { Animatable(0f) }
    
    // Auto navigate back to Home after 2 seconds
    LaunchedEffect(Unit) {
        // Clear cart
        viewModel.clearCart()
        
        // Start scale animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        // Wait for 2 seconds then navigate back to Home
        delay(2000)
        navController.navigate("Home") {
            popUpTo("Home") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success icon with animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9800))
                    .scale(scale.value)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = "Success",
                    modifier = Modifier.size(72.dp),
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Success message
            Text(
                text = "Thanh Toán Thành Công !!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Bạn đã mua hàng thành công !!",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Trở về trang Home .....",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
} 