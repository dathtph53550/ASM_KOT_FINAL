package com.example.asm_1.service

import com.example.asm_1.models.CartItem
import com.example.asm_1.models.CartItemTypeAdapter
import com.example.asm_1.models.Category
import com.example.asm_1.models.CategoryTypeAdapter
import com.example.asm_1.models.Order
import com.example.asm_1.models.Product
import com.example.asm_1.models.ProductTypeAdapter
import com.example.asm_1.models.User
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("carts")
    suspend fun getCartItems(): List<CartItem>

    @POST("carts")
    suspend fun addToCart(@Body cartItem: CartItem): CartItem

    @PUT("carts/{id}")
    suspend fun updateCartItem(@Path("id") id: String, @Body cartItem: CartItem): CartItem

    @DELETE("carts/{id}")
    suspend fun removeFromCart(@Path("id") id: String)

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @GET("orders")
    suspend fun getOrders(): List<Order>

    @GET("products")
    suspend fun searchProducts(@Query("q") query: String): List<Product>

    @POST("users")
    suspend fun registerUser(@Body user: User): User

    @GET("users")
    suspend fun getUsers(): List<User>
    
    // Product management endpoints
    @POST("products")
    suspend fun addProduct(@Body product: Product): Response<Product>
    
    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Response<Product>
    
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Void>
    
    // Order management endpoint
    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") id: String, @Body order: Order): Response<Order>
}

object RetrofitInstance {
    const val BASE_URL = "http://10.24.34.205:3000/"
    val api: ApiService by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(Product::class.java, ProductTypeAdapter())
            .registerTypeAdapter(Category::class.java, CategoryTypeAdapter())
            .registerTypeAdapter(CartItem::class.java, CartItemTypeAdapter())
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}