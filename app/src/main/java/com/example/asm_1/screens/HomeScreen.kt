package com.example.asm_1.screens
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.R
import com.example.asm_1.models.Product
import com.example.asm_1.service.ViewModelApp

@Composable
fun HomeScreen(navController: NavHostController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(navController, Modifier.padding(innerPadding))
    }
}



@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.homee),
        BottomNavItem("Cart", R.drawable.cart),
        BottomNavItem("Message", R.drawable.mes),
        BottomNavItem("Account", R.drawable.acc)
    )

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .padding(start = 1.dp, end = 1.dp)
            .border(1.dp, Color.White, shape = RoundedCornerShape(15.dp))
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(10.dp))
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.label
            val iconTint = when {
                isSelected && item.label == "Account" -> Color(0xFFFF9800)
                isSelected -> Color(0xFFFF9800)
                else -> Color.Gray
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        tint = iconTint
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFFFF9800) else Color.Gray
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = iconTint,
                    unselectedIconColor = Color.Gray
                ),
                onClick = {
                    navController.navigate(item.label) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


data class BottomNavItem(val label: String, val iconRes: Int)

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("Home") { HomeScreenContent(navController) }
        composable("Cart") { CartScreen(navController = navController) }
        composable("Message") { MessageScreen(navController) }
        composable("Account") { ProfileScreen(navController) }
        composable("noti") { NotificationScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("Detail/{id}/{name}/{image}/{rating}/{distance}/{price}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "0"
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val encodedImage = backStackEntry.arguments?.getString("image") ?: ""
            // Decode the URL that was encoded for navigation
            val image = java.net.URLDecoder.decode(encodedImage, "UTF-8")
            val rating = backStackEntry.arguments?.getString("rating") ?: ""
            val distance = backStackEntry.arguments?.getString("distance") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: ""

            DetailScreen(Product(
                id = id,
                name = name,
                image = image, // Decoded image URL 
                rating = rating,
                distance = distance,
                price = price,
                description = "Delicious $name available at our restaurant. Made with fresh ingredients and prepared by our expert chefs."
            ), navController)
        }
        composable("Payment") { backStackEntry ->
            PaymentScreen(navController, backStackEntry)
        }
        composable("Success") {
            SuccessScreen(navController)
        }
        composable("Profile") {
            SettingProfileScreen(navController)
        }
        composable("Setting") {
            SettingsScreen(navController)
        }
        composable("Chat") {
            ChatScreen(navController)
        }
        composable("History") {
            HistoryScreen(navController)
        }
    }
}

@Composable
fun HomeScreenContent(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HomeTopSection(navController = navController)
        Spacer(modifier = Modifier.height(16.dp))
        CategorySection()
        Spacer(modifier = Modifier.height(16.dp))
        FoodListSection(navController)
    }
}

@Composable
fun HomeTopSection(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.food_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Your Location", color = Color.White, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Location",
                            tint = Color.White
                        )
                        Text(
                            text = "Thành Phố Hà Nội",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.dropdown),
                            contentDescription = "Dropdown",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp).padding(start = 4.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.sea),
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { navController.navigate("noti") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.noti),
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Chọn món ăn mà bạn yêu thích nhất",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CategorySection(viewModelApp: ViewModelApp = viewModel()) {
    val categories by viewModelApp.listCategory
    val selectedCategory by viewModelApp.selectedCategory

    LaunchedEffect(Unit) {
        viewModelApp.getListCategory()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tìm Doanh Mục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Xem tất cả", color = Color(0xFFFF9800), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            item {
                CategoryItem(
                    name = "All",
                    iconRes = "https://i.pinimg.com/474x/a3/22/5c/a3225c3afbdf3bca5ac4b730151a25ef.jpg",
                    isSelected = selectedCategory == "All",
                    onSelect = { viewModelApp.selectCategory("All") }
                )
            }
            items(categories ?: emptyList()) { category ->
                CategoryItem(
                    name = category.name,
                    iconRes = category.image,
                    isSelected = selectedCategory == category.name,
                    onSelect = { viewModelApp.selectCategory(category.name) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    iconRes: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(
                if (isSelected) Color(0xFFFF9800) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(12.dp)
    ) {
            // Load from URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconRes)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier.size(32.dp)
            )

        Text(
            text = name,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}




@Composable
fun FoodCard(food: Product, navController: NavHostController) {
    Log.d("mmmmm", "FoodCard: " + food)
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.White)
            .clickable {
                // Encode the image URL to avoid issues with slashes in navigation
                val encodedImage = java.net.URLEncoder.encode(food.image, "UTF-8")
                navController.navigate("Detail/${food.id}/${food.name}/${encodedImage}/${food.rating}/${food.distance}/${food.price}")
            }
        ,
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

            Text(food.price + " đ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
        }
    }
}

@Composable
fun FoodListSection(navController: NavHostController, viewModelApp: ViewModelApp = viewModel()) {
    val filteredProducts by viewModelApp.filteredProducts

    LaunchedEffect(Unit) {
        viewModelApp.getListProduct()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        items(filteredProducts ?: emptyList()) { food ->
            FoodCard(food, navController = navController)
        }
    }
}




