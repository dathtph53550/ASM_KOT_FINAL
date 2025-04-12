package com.example.asm_1

import LoginScreen
import RegisterScreen
import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.asm_1.screens.AdminScreen
import com.example.asm_1.screens.DetailScreen
import com.example.asm_1.screens.HomeScreen
import com.example.asm_1.screens.ManHinhChao
import com.example.asm_1.screens.NotificationScreen
import com.example.asm_1.screens.ProfileScreen
import com.example.asm_1.ui.theme.ASM_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    
    // Check if we should show the login screen directly
    val showLogin = activity?.intent?.getBooleanExtra("SHOW_LOGIN", false) ?: false
    val startDestination = if (showLogin) "login" else "splash"
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            ManHinhChao(navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("account") {
            ProfileScreen(navController)
        }
        composable("admin") {
            AdminScreen(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ASM_1Theme {
        HomeScreen(navController = rememberNavController())
    }
}