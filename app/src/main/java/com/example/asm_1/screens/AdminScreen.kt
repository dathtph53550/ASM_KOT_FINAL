package com.example.asm_1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Fastfood
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.asm_1.models.Product
import com.example.asm_1.service.ViewModelApp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State to track which admin section is selected
    var selectedSection by remember { mutableStateOf("products") }

    // Fetch products and orders when entering the admin screen
    LaunchedEffect(Unit) {
        viewModel.getListProduct()
        viewModel.getOrders()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Admin Quản Lý",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Divider()

                // Drawer items
                NavigationDrawerItem(
                    label = { Text("Quản Lý Sản Phẩm") },
                    selected = selectedSection == "products",
                    onClick = {
                        selectedSection = "products"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = "Products") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Quản Lý Orders") },
                    selected = selectedSection == "orders",
                    onClick = {
                        selectedSection = "orders"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Orders") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.weight(1f))

                Divider()
                NavigationDrawerItem(
                    label = { Text("Đăng Xuất") },
                    selected = false,
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                if (selectedSection == "products") "Quản Lý Sản Phẩm" else "Quản Lý Orders"
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedSection) {
                        "products" -> ProductManagementSection(viewModel)
                        "orders" -> OrderManagementSection(viewModel)
                    }
                }
            }
        }
    )
}

@Composable
fun ProductManagementSection(viewModel: ViewModelApp) {
    val products = viewModel.listProduct.value
    val productActionResult by viewModel.productActionResult.collectAsState()
    
    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    
    // Show snackbar for results
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Observe product action results
    LaunchedEffect(productActionResult) {
        productActionResult?.let { result ->
            result.fold(
                onSuccess = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onFailure = { error ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: ${error.message}")
                    }
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Danh Sách Sản Phẩm",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm Sản Phẩm")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có sản phẩm")
                }
            } else {
                LazyColumn {
                    items(products) { product ->
                        val categories = viewModel.listCategory.value
                        val category = categories?.find { it.id == product.categoryId }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                // Product header with name and actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Row {
                                        IconButton(onClick = { 
                                            selectedProduct = product
                                            showEditDialog = true 
                                        }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = Color(0xFFFF9800)
                                            )
                                        }

                                        IconButton(onClick = { 
                                            selectedProduct = product
                                            showDeleteConfirmation = true 
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Product details with image
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Product image
                                    if (product.image.startsWith("http")) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(product.image)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        // Fallback to placeholder if not a URL
                                        Box(
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Image,
                                                contentDescription = "No Image",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    // Product information
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Giá: ${product.price}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF9800),
                                            fontSize = 16.sp
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Row {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Rating",
                                                tint = Color(0xFFFFB300),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(product.rating ?: "4.5")
                                            
                                            Spacer(modifier = Modifier.width(12.dp))
                                            
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = "Distance",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(product.distance ?: "500m")
                                        }
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        // Category
                                        Row {
                                            Icon(
                                                Icons.Default.Category,
                                                contentDescription = "Category",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                category?.name ?: "Danh mục #${product.categoryId}",
                                                color = Color.Gray
                                            )
                                        }
                                        
                                        if (product.description != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                product.description,
                                                fontSize = 12.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Snackbar host at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
    
    // Add Product Dialog
    if (showAddDialog) {
        ProductDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, price, imageUrl, categoryId ->
                viewModel.addProduct(
                    name = name, 
                    price = price, 
                    imageUrl = imageUrl, 
                    rating = "4.5", 
                    distance = "500m", 
                    description = "Delicious food available at our restaurant", 
                    categoryId = categoryId
                )
                showAddDialog = false
            }
        )
    }
    
    // Edit Product Dialog
    if (showEditDialog && selectedProduct != null) {
        ProductDialog(
            product = selectedProduct,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, price, imageUrl, categoryId ->
                val updatedProduct = selectedProduct!!.copy(
                    name = name,
                    price = price,
                    image = imageUrl,
                    categoryId = categoryId
                )
                viewModel.updateProduct(updatedProduct)
                showEditDialog = false
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmation && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm '${selectedProduct?.name}' này không?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(selectedProduct!!.id)
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: String, imageUrl: String, categoryId: String) -> Unit,
    viewModel: ViewModelApp = viewModel()
) {
    // Main product fields
    var name by remember { mutableStateOf(product?.name ?: "") }
    var price by remember { mutableStateOf(product?.price ?: "") }
    var imageUrl by remember { mutableStateOf(product?.image ?: "") }
    
    // Additional product fields
    var rating by remember { mutableStateOf(product?.rating ?: "4.5") }
    var distance by remember { mutableStateOf(product?.distance ?: "500m") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var categoryId by remember { mutableStateOf(product?.categoryId ?: "1") }
    
    // Field validation states
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var imageUrlError by remember { mutableStateOf(false) }
    
    // Preview image when URL changes
    var previewVisible by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (product == null) "Thêm Sản Phẩm Mới" else "Cập Nhật Sản Phẩm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name field
                TextField(
                    value = name,
                    onValueChange = { 
                        name = it 
                        nameError = it.isEmpty()
                    },
                    label = { Text("Tên sản phẩm") },
                    placeholder = { Text("Nhập tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                if (nameError) {
                    Text("Vui lòng nhập tên sản phẩm", color = Color.Red, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Price field
                TextField(
                    value = price,
                    onValueChange = { 
                        price = it 
                        priceError = it.isEmpty()
                    },
                    label = { Text("Giá") },
                    placeholder = { Text("Nhập giá sản phẩm") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = priceError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                if (priceError) {
                    Text("Vui lòng nhập giá sản phẩm", color = Color.Red, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Image URL field
                TextField(
                    value = imageUrl,
                    onValueChange = { 
                        imageUrl = it 
                        imageUrlError = it.isEmpty() || !it.startsWith("http")
                        previewVisible = it.isNotEmpty() && it.startsWith("http")
                    },
                    label = { Text("URL Hình ảnh") },
                    placeholder = { Text("Nhập URL hình ảnh") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = imageUrlError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                if (imageUrlError) {
                    Text(
                        if (imageUrl.isEmpty()) "Vui lòng nhập URL hình ảnh" 
                        else "URL hình ảnh phải bắt đầu bằng 'http'", 
                        color = Color.Red, 
                        fontSize = 12.sp
                    )
                }
                
                // Image preview
                if (previewVisible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Image Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rating field (optional)
                TextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Đánh giá") },
                    placeholder = { Text("VD: 4.5") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Distance field (optional)
                TextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Khoảng cách") },
                    placeholder = { Text("VD: 500m") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Category dropdown field with default categories
                val defaultCategories = listOf(
                    "1" to "Burger",
                    "2" to "Pizza",
                    "3" to "Taco",
                    "4" to "Drink"
                )
                
                var expanded by remember { mutableStateOf(false) }
                // Initialize with correct mapping
                val selectedCategoryName = defaultCategories.find { it.first == categoryId }?.second ?: "Chọn danh mục"
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Danh mục",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFF9800),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedCategoryName)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            defaultCategories.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        categoryId = id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Description field (optional)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    placeholder = { Text("Mô tả sản phẩm (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFF9800),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    minLines = 2
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            // Validate fields
                            nameError = name.isEmpty()
                            priceError = price.isEmpty()
                            imageUrlError = imageUrl.isEmpty() || !imageUrl.startsWith("http")
                            
                            if (!nameError && !priceError && !imageUrlError) {
                                // Create new product with all fields
                                val updatedProduct = product?.copy(
                                    name = name,
                                    price = price,
                                    image = imageUrl,
                                    rating = rating,
                                    distance = distance,
                                    description = description,
                                    categoryId = categoryId
                                ) ?: Product(
                                    id = "", // Will be assigned in viewModel
                                    name = name,
                                    price = price,
                                    image = imageUrl,
                                    rating = rating,
                                    distance = distance,
                                    description = description,
                                    categoryId = categoryId
                                )
                                
                                // Pass all fields to the addProduct function via onConfirm
                                onConfirm(name, price, imageUrl, categoryId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text(if (product == null) "Thêm" else "Cập nhật")
                    }
                }
            }
        }
    }
}

@Composable
fun OrderManagementSection(viewModel: ViewModelApp) {
    val orders = viewModel.orders.collectAsState().value
    val orderUpdateResult by viewModel.orderUpdateResult.collectAsState()
    
    // Dialog states
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<com.example.asm_1.models.Order?>(null) }
    
    // Show snackbar for results
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Observe order update results
    LaunchedEffect(orderUpdateResult) {
        orderUpdateResult?.let { result ->
            result.fold(
                onSuccess = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onFailure = { error ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: ${error.message}")
                    }
                }
            )
        }
    }

    // Fetch orders when component loads
    LaunchedEffect(Unit) {
        viewModel.getOrders()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Quản lý đơn hàng",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                "${orders.size} Đơn hàng",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có đơn hàng")
                }
            } else {
                LazyColumn {
                    items(orders.sortedByDescending { it.createAt }) { order ->
                        OrderAdminItem(order, onStatusUpdate = {
                            selectedOrder = order
                            showStatusDialog = true
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        
        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    
    // Order Status Dialog
    if (showStatusDialog && selectedOrder != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Cập nhật trạng thái đơn hàng") },
            text = { 
                Column {
                    Text("Chọn trạng thái mới cho đơn hàng #${formatOrderId(selectedOrder!!)}") 
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons moved to text content
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateOrderStatus(selectedOrder!!, "confirmed")
                                showStatusDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("Xác nhận")
                        }
                        
                        Button(
                            onClick = {
                                viewModel.updateOrderStatus(selectedOrder!!, "cancelled")
                                showStatusDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Hủy đơn")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showStatusDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun OrderAdminItem(order: com.example.asm_1.models.Order, onStatusUpdate: () -> Unit) {
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
                        text = "Order #${formatOrderId(order)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatDate(order.createAt),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    
                    IconButton(onClick = onStatusUpdate) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Status",
                            tint = Color(0xFFFF9800)
                        )
                    }
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
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        // Fallback icon
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = item.name,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${item.quantity}x",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Text(
                        text = item.price,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Customer Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    CustomerInfoRow("Khách hàng", order.nameOrder)
                    CustomerInfoRow("Số điện thoại", order.phoneOrder)
                    CustomerInfoRow("Địa chỉ", order.addressOrder)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Tổng tiền",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        calculateTotal(order),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerInfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

private fun formatOrderId(order: com.example.asm_1.models.Order): String {
    return (formatDate(order.createAt).hashCode().absoluteValue % 1000).toString()
}

private fun formatDate(dateString: String): String {
    return try {
        if (dateString.contains("T")) {
            val offsetDateTime = OffsetDateTime.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            offsetDateTime.format(formatter)
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

private fun calculateTotal(order: com.example.asm_1.models.Order): String {
    val total = order.order.sumOf { 
        try {
            val price = it.price.replace(".", "").replace(",", "").toDouble()
            (price * it.quantity)
        } catch (e: Exception) {
            0.0
        }
    }
    return "${String.format("%,.0f", total)} đ"
}
