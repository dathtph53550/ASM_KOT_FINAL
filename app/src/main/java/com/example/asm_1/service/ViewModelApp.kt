package com.example.asm_1.service

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asm_1.models.CartItem
import com.example.asm_1.models.Category
import com.example.asm_1.models.Order
import com.example.asm_1.models.OrderItem
import com.example.asm_1.models.Product
import com.example.asm_1.models.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModelApp : ViewModel() {
    private val _listProduct = mutableStateOf<List<Product>?>(null)
    var listProduct: State<List<Product>?> = _listProduct

    private val _listCategory = mutableStateOf<List<Category>?>(null)
    var listCategory: State<List<Category>?> = _listCategory

    private val _selectedCategory = mutableStateOf<String?>("All")
    val selectedCategory: State<String?> = _selectedCategory

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _filteredProducts = mutableStateOf<List<Product>?>(null)
    val filteredProducts: State<List<Product>?> = _filteredProducts

    private val _listCart = mutableStateOf<List<CartItem>?>(null)
    var listCart: State<List<CartItem>?> = _listCart

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _registerResult = MutableStateFlow<Result<User>?>(null)
    val registerResult: StateFlow<Result<User>?> = _registerResult



    fun getListProduct(){
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting API call to ${RetrofitInstance.BASE_URL}products")
                val response = RetrofitInstance.api.getProducts()
                Log.d("API_DEBUG", "API call successful. Received ${response.size} products")
                _listProduct.value = response
                filterProducts() // Filter products after fetching
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch products", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun getListCategory() {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting API call to ${RetrofitInstance.BASE_URL}categories")
                val response = RetrofitInstance.api.getCategories()
                Log.d("API_DEBUG", "API call successful. Received ${response.size} categories")
                _listCategory.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch categories", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun getCartItems() {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting API call to ${RetrofitInstance.BASE_URL}carts")
                val response = RetrofitInstance.api.getCartItems()
                Log.d("API_DEBUG", "API call successful. Received ${response.size} cart items")
                _listCart.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch cart items", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting API call to add cart item")

                // Lấy danh sách giỏ hàng hiện tại
                val currentCartItems = RetrofitInstance.api.getCartItems()
                
                // Tìm sản phẩm tương tự trong giỏ hàng dựa trên tên và hình ảnh
                val existingItem = currentCartItems.find { 
                    it.name == cartItem.name && it.image == cartItem.image 
                }
                
                if (existingItem != null) {
                    // Sản phẩm đã tồn tại
                    Log.d("API_DEBUG", "Found existing item with id: ${existingItem.id}, removing it first")
                    // 1. Xóa sản phẩm cũ
                    RetrofitInstance.api.removeFromCart(existingItem.id!!)
                    
                    // 2. Thêm sản phẩm mới với số lượng đã cập nhật
                    val updatedItem = CartItem(
                        id = existingItem.id, // JSON Server sẽ gán ID mới
                        name = existingItem.name,
                        image = existingItem.image,
                        price = existingItem.price,
                        quantity = existingItem.quantity + cartItem.quantity,
                        isChecked = existingItem.isChecked
                    )
                    
                    Log.d("API_DEBUG", "Adding updated cart item with quantity: ${updatedItem.quantity}")
                    RetrofitInstance.api.addToCart(updatedItem)
                } else {
                    // Sản phẩm mới hoàn toàn, thêm mới vào giỏ hàng
                    Log.d("API_DEBUG", "Adding new cart item with quantity: ${cartItem.quantity}")
                    RetrofitInstance.api.addToCart(cartItem)
                }
                
                // Làm mới danh sách giỏ hàng
                getCartItems()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to add item to cart", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun updateCartItem(id: String, cartItem: CartItem) {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting to update cart item with id: $id")
                // Gọi API để cập nhật sản phẩm
                RetrofitInstance.api.updateCartItem(id, cartItem)
                Log.d("API_DEBUG", "Update completed successfully")
                // Làm mới danh sách giỏ hàng
                getCartItems()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to update cart item", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun removeFromCart(id: String) {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting API call to remove item from cart with id: $id using query parameter")
                try {
                    RetrofitInstance.api.removeFromCart(id)
                    Log.d("API_DEBUG", "API call successful. Item removed from cart")
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Failed to remove item using query parameter, trying alternative approach", e)
                    
                    // Cách tiếp cận thay thế: Lấy toàn bộ giỏ hàng, lọc bỏ mục cần xóa và gửi lại
                    try {
                        val currentCartItems = RetrofitInstance.api.getCartItems()
                        val filteredItems = currentCartItems.filter { it.id != id }
                        
                        // Xóa các mục cũ bằng cách gọi addToCart với danh sách mới
                        // (JSON Server không hỗ trợ xóa toàn bộ giỏ hàng cùng lúc)
                        for (item in currentCartItems) {
                            if (item.id != id) {
                                continue // Bỏ qua các mục không cần xóa
                            }
                            
                            try {
                                // Chỉ cố gắng xóa mục cần xóa
                                RetrofitInstance.api.removeFromCart(item.id)
                            } catch (e3: Exception) {
                                Log.e("API_ERROR", "Failed to remove specific item: ${item.id}", e3)
                            }
                        }
                        
                        Log.d("API_DEBUG", "Alternative approach successful")
                    } catch (e2: Exception) {
                        Log.e("API_ERROR", "Alternative approach also failed", e2)
                        throw e2 // Ném lại ngoại lệ để xử lý bên ngoài
                    }
                }
                
                // Làm mới danh sách giỏ hàng
                getCartItems()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to remove item from cart", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    // Xóa toàn bộ giỏ hàng
    fun clearCart() {
        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Starting to clear entire cart")
                val currentCartItems = RetrofitInstance.api.getCartItems()
                
                // Lưu lại danh sách id trước khi xóa
                val itemIds = currentCartItems.map { it.id }
                
                var successCount = 0
                var failureCount = 0
                
                // Xóa từng sản phẩm trong giỏ hàng
                for (id in itemIds) {
                    try {
                        Log.d("API_DEBUG", "Removing item with id: $id")
                        RetrofitInstance.api.removeFromCart(id!!)
                        successCount++
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Failed to remove item with id: $id", e)
                        failureCount++
                        
                        // Cố gắng một cách khác nếu việc xóa thất bại
                        try {
                            // Lấy lại toàn bộ danh sách sau lỗi
                            val remainingItems = RetrofitInstance.api.getCartItems()
                            val itemToRemove = remainingItems.find { it.id == id }
                            
                            if (itemToRemove != null) {
                                // Thử cập nhật sản phẩm với số lượng = 0 thay vì xóa
                                val updatedItem = CartItem(
                                    id = itemToRemove.id,
                                    name = itemToRemove.name,
                                    image = itemToRemove.image,
                                    price = itemToRemove.price,
                                    quantity = 0, // Đặt số lượng = 0
                                    isChecked = itemToRemove.isChecked
                                )
                                
                                // Thêm sản phẩm mới với số lượng = 0
                                RetrofitInstance.api.addToCart(updatedItem)
                                Log.d("API_DEBUG", "Alternative approach for item $id: updated quantity to 0")
                            }
                        } catch (e2: Exception) {
                            Log.e("API_ERROR", "Alternative approach for item $id also failed", e2)
                        }
                    }
                }
                
                // Làm mới danh sách giỏ hàng
                getCartItems()
                Log.d("API_DEBUG", "Cart clearing completed. Success: $successCount, Failures: $failureCount")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to clear cart", e)
                Log.e("API_ERROR", "Error message: ${e.message}")
                Log.e("API_ERROR", "Error cause: ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    fun sendOrder(name: String, address: String, phone: String) {
        viewModelScope.launch {
            try {
                val orderItems = listCart.value?.map { item ->
                    OrderItem(
                        name = item.name,
                        image = item.image, // Keep the original image URL without prefixing
                        price = item.price,
                        quantity = item.quantity
                    )
                } ?: emptyList()

                val order = Order(
                    nameOrder = name,
                    addressOrder = address,
                    phoneOrder = phone,
                    order = orderItems
                )

                val response = RetrofitInstance.api.createOrder(order)
                if (response.isSuccessful) {
                    // Clear cart after successful order
                    clearCart()
                }
            } catch (e: Exception) {
                Log.e("ViewModelApp", "Error sending order: ${e.message}")
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getOrders()
                _orders.value = response
            } catch (e: Exception) {
                Log.e("ViewModelApp", "Error fetching orders: ${e.message}")
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        filterProducts()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    private fun filterProducts() {
        val currentCategory = _selectedCategory.value
        val currentQuery = _searchQuery.value.lowercase()
        
        _filteredProducts.value = _listProduct.value?.filter { product ->
            val matchesCategory = currentCategory == "All" || product.categoryId == getCategoryId(currentCategory ?: "All")
            val matchesSearch = product.name.lowercase().contains(currentQuery)
            matchesCategory && matchesSearch
        }
    }

    private fun getCategoryId(categoryName: String): String {
        return when (categoryName) {
            "Burger" -> "1"
            "Pizza" -> "2"
            "Taco" -> "3"
            "Drink" -> "4"
            else -> "1"
        }
    }

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                // Kiểm tra email và username đã tồn tại chưa
                val existingUsers = RetrofitInstance.api.getUsers()
                
                // Kiểm tra email trùng
                if (existingUsers.any { it.email.equals(email, ignoreCase = true) }) {
                    _registerResult.value = Result.failure(Exception("Email trùng"))
                    return@launch
                }
                // Nếu không trùng, tiến hành đăng ký
                val user = User(
                    email = email,
                    username = username,
                    password = password
                )
                val response = RetrofitInstance.api.registerUser(user)
                _registerResult.value = Result.success(response)
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Registration failed", e)
            }
        }
    }

    // Add these properties
    private val _loginResult = MutableStateFlow<Result<User>?>(null)
    val loginResult: StateFlow<Result<User>?> = _loginResult
    
    // Product management responses
    private val _productActionResult = MutableStateFlow<Result<String>?>(null)
    val productActionResult: StateFlow<Result<String>?> = _productActionResult
    
    // Order management response
    private val _orderUpdateResult = MutableStateFlow<Result<String>?>(null)
    val orderUpdateResult: StateFlow<Result<String>?> = _orderUpdateResult

    // Add login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val users = RetrofitInstance.api.getUsers()
                val user = users.find { 
                    it.email.equals(email, ignoreCase = true) && 
                    it.password == password 
                }
                
                if (user != null) {
                    _loginResult.value = Result.success(user)
                } else {
                    _loginResult.value = Result.failure(Exception("Invalid email or password"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Login failed", e)
            }
        }
    }
    
    // Add a new product
    fun addProduct(name: String, price: String, imageUrl: String, rating: String = "4.5", 
                   distance: String = "500m", description: String = "Delicious food", categoryId: String = "1") {
        viewModelScope.launch {
            try {
                val product = Product(
                    id = java.util.UUID.randomUUID().toString().substring(0, 4),
                    name = name,
                    image = imageUrl,
                    rating = rating,
                    distance = distance,
                    price = price,
                    description = description,
                    categoryId = categoryId
                )
                
                val response = RetrofitInstance.api.addProduct(product)
                if (response.isSuccessful) {
                    // Refresh product list
                    getListProduct()
                    _productActionResult.value = Result.success("Product added successfully")
                } else {
                    _productActionResult.value = Result.failure(Exception("Failed to add product: ${response.message()}"))
                }
            } catch (e: Exception) {
                _productActionResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Add product failed", e)
            }
        }
    }
    
    // Update an existing product
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                // Check if product has an ID
                if (product.id.isNullOrEmpty()) {
                    _productActionResult.value = Result.failure(Exception("Product ID is missing"))
                    return@launch
                }
                
                val response = RetrofitInstance.api.updateProduct(product.id, product)
                if (response.isSuccessful) {
                    // Refresh product list
                    getListProduct()
                    _productActionResult.value = Result.success("Product updated successfully")
                } else {
                    _productActionResult.value = Result.failure(Exception("Failed to update product: ${response.message()}"))
                }
            } catch (e: Exception) {
                _productActionResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Update product failed", e)
            }
        }
    }
    
    // Delete a product
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteProduct(productId)
                if (response.isSuccessful) {
                    // Refresh product list
                    getListProduct()
                    _productActionResult.value = Result.success("Product deleted successfully")
                } else {
                    _productActionResult.value = Result.failure(Exception("Failed to delete product: ${response.message()}"))
                }
            } catch (e: Exception) {
                _productActionResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Delete product failed", e)
            }
        }
    }
    
    // Update order status
    fun updateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            try {
                // Check if order has an ID
                if (order.id.isNullOrEmpty()) {
                    _orderUpdateResult.value = Result.failure(Exception("Order ID is missing"))
                    return@launch
                }
                
                // Create updated order with new status
                val updatedOrder = order.copy(status = newStatus)
                
                // Use the order's ID for the update
                val response = RetrofitInstance.api.updateOrder(order.id, updatedOrder)
                if (response.isSuccessful) {
                    // Refresh orders list
                    getOrders()
                    _orderUpdateResult.value = Result.success("Order status updated to $newStatus")
                } else {
                    _orderUpdateResult.value = Result.failure(Exception("Failed to update order status: ${response.message()}"))
                }
            } catch (e: Exception) {
                _orderUpdateResult.value = Result.failure(e)
                Log.e("ViewModelApp", "Update order status failed", e)
            }
        }
    }

}




