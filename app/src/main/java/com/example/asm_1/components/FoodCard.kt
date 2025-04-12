package com.example.asm_1.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.Product

@Composable
fun FoodCard(food: Product, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    "Detail/${food.id}/${food.name}/${food.image}/${food.rating}/${food.distance}/${food.price}"
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box {
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
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // For backward compatibility with resource IDs stored as strings
                    Image(
                        painter = painterResource(id = R.drawable.burger_1),
                        contentDescription = food.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    tint = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(food.name, fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Rating",
                    tint = Color(0xFFFFA500),
                    modifier = Modifier.size(16.dp)
                )
                Text(food.rating, fontSize = 12.sp)
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "Distance",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(food.distance, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                food.price,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
        }
    }
}

