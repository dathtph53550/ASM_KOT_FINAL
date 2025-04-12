package com.example.asm_1.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.asm_1.models.CartItem
import com.example.asm_1.models.Product
import com.example.asm_1.service.ViewModelApp

//data class Product(
//    val id: Int = 0,
//    val name: String,
//    val image: Int,
//    val rating: String,
//    val distance: String,
//    val price: String,
//    val description: String = "Delicious food available at our restaurant. Made with fresh ingredients and prepared by our expert chefs."
//)



@Composable
fun DetailScreen(food: com.example.asm_1.models.Product, navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    var quantity by remember { mutableStateOf(1) }
    val scrollState = rememberScrollState()

    Log.d("oooo", "DetailScreen: "+ food)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with back button and share
        Box(modifier = Modifier.fillMaxWidth()) {
            // Check if the image is a URL or resource reference
            if (food.image.startsWith("http")) {
                // Load image from URL using AsyncImage
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(food.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = food.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // For backward compatibility with resource IDs stored as strings
                Image(
                    painter = painterResource(id = R.drawable.burger_1),
                    contentDescription = food.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Top bar with back and share buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                
                IconButton(
                    onClick = { /* Share functionality */ },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Black
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Title and Price
            Text(
                text = food.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = food.price + " đ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rating and Distance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = food.rating,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Distance",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = food.distance,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Description
            Text(
                text = "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = food.description,
                fontSize = 16.sp,
                color = Color.Gray,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quantity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = Color.Black
                        )
                    }
                    
                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Add to Cart Button
            Button(
                onClick = { 
                    // Sử dụng phương thức addToCart đã được cập nhật
                    val cartItem = CartItem(
                        id = food.id, // The API will assign an ID
                        name = food.name,
                        image = food.image,
                        price = food.price,
                        quantity = quantity,
                        isChecked = true
                    )
                    viewModel.addToCart(cartItem)
                    navController.navigate("Cart")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Cart",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
fun ProductImageSection(imageUrl: String, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Check if the image is a URL or resource reference
        if (imageUrl.startsWith("http")) {
            // Load image from URL using AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback to a default image
            Image(
                painter = painterResource(id = R.drawable.burger_1),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            IconButton(onClick = { /* Share */ }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
            }
        }
    }
}


@Composable
fun ProductInfoSection(product: Product) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = product.price, fontSize = 18.sp, color = Color(0xFFFF9800))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Time", tint = Color.Gray)
            Text(" ${product.distance}", color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
            Text(" ${product.rating}", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = product.description, fontSize = 14.sp, color = Color.Gray)
    }
}


@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
        }
        Text(text = "$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { onQuantityChange(quantity + 1) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
        }
    }
}

@Composable
fun AddToCartButton(quantity: Int, price: String) {
    Button(
        onClick = { /* Thêm vào giỏ hàng */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
        )
    ) {
        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add to Cart", fontSize = 16.sp, color = Color.White)
    }
}
