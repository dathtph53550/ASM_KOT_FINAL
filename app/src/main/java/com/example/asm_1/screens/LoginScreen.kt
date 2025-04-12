import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asm_1.R

// Add these imports
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asm_1.service.ViewModelApp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Collect login result
    val loginResult by viewModel.loginResult.collectAsState()

    // Handle login result
    LaunchedEffect(loginResult) {
        loginResult?.fold(
            onSuccess = { user ->
                Toast.makeText(context, "Đăng nhập thành công !!", Toast.LENGTH_SHORT).show()
                // Check if this is admin login
                if (user.email == "admin" && user.password == "admin") {
                    navController.navigate("admin")
                } else {
                    navController.navigate("home")
                }
            },
            onFailure = { error ->
                Toast.makeText(context, "Đăng Nhập Thất Bại ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "Login to your account.",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Please sign in to your account",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        Text(
            "Forgot password?",
            color = Color(0xFFFF9800),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* Handle forgot password */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Update Sign In Button
        Button(
            onClick = {
                if (email.text.isNotBlank() && password.text.isNotBlank()) {
                    // Check for admin credentials directly
                    if (email.text == "admin" && password.text == "admin") {
                        // For admin login, bypass the normal login process
                        navController.navigate("admin")
                    } else {
                        // Regular login process
                        viewModel.login(email.text, password.text)
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign In", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Or sign in with
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text("  Or sign in with  ", fontSize = 14.sp, color = Color.Gray)
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Social Login Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SocialLoginButton(iconResId = R.drawable.google_logo)
            SocialLoginButton(iconResId = R.drawable.facebook_logo)
            SocialLoginButton(iconResId = R.drawable.apple_logo)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register
        Row {
            Text("Don't have an account?", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Register",
                fontSize = 14.sp,
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }
    }
}

@Composable
fun SocialLoginButton(iconResId: Int) {
    IconButton(
        onClick = { /* Handle Social Login */ },
        modifier = Modifier
            .size(48.dp)
            .background(Color.LightGray, RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Social Login",
            modifier = Modifier.size(48.dp)
        )
    }
}

