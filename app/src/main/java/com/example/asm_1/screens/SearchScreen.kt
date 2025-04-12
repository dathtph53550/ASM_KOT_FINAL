package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.Product
import com.example.asm_1.service.ViewModelApp

@Composable
fun SearchScreen(navController: NavHostController, viewModelApp: ViewModelApp = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts by viewModelApp.filteredProducts

    LaunchedEffect(Unit) {
        viewModelApp.getListProduct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                "Search Food",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModelApp.setSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
            placeholder = { Text("Search Food") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "Filter",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                disabledContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        // Category Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CategoryIcon(R.drawable.burger_icon, "Burger") {
                viewModelApp.selectCategory("Burger")
            }
            CategoryIcon(R.drawable.taco_icon, "Taco") {
                viewModelApp.selectCategory("Taco")
            }
            CategoryIcon(R.drawable.drink_icon, "Drink") {
                viewModelApp.selectCategory("Drink")
            }
            CategoryIcon(R.drawable.pizza_icon, "Pizza") {
                viewModelApp.selectCategory("Pizza")
            }
        }

        // Search Results
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredProducts ?: emptyList()) { product ->
                FooddCard(product, navController)
            }
        }
    }
}

@Composable
fun CategoryIcon(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(Color(0xFFFF9800), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun FooddCard(food: Product, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                // Encode the image URL to avoid issues with slashes in navigation
                val encodedImage = java.net.URLEncoder.encode(food.image, "UTF-8")
                navController.navigate(
                    "Detail/${food.id}/${food.name}/${encodedImage}/${food.rating}/${food.distance}/${food.price}"
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