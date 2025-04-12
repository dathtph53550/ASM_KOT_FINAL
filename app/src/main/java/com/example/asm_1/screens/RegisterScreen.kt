import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asm_1.R
import com.example.asm_1.models.User
import com.example.asm_1.service.RetrofitInstance
// Add this import at the top of the file with other imports
import com.example.asm_1.service.ViewModelApp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: ViewModelApp = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isTermsChecked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Collect registration result
    val registerResult by viewModel.registerResult.collectAsState()

    // Handle registration result
    LaunchedEffect(registerResult) {
        registerResult?.fold(
            onSuccess = {
                Toast.makeText(context, "Đăng Ký Thành Công !!", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
            },
            onFailure = { error ->
                Toast.makeText(context, "Đăng Ký Thất Bại !! ${error.message}", Toast.LENGTH_LONG).show()
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
            "Create your new account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Create an account to start looking for the food you like",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
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

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name") },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Terms Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermsChecked,
                onCheckedChange = { isTermsChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFF9800)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                buildAnnotatedString {
                    append("I Agree with ")
                    withStyle(style = SpanStyle(color = Color(0xFFFF9800))) {
                        append("Terms of Service")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(color = Color(0xFFFF9800))) {
                        append("Privacy Policy")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.text.isNotBlank() && username.text.isNotBlank() && password.text.isNotBlank()) {
                    viewModel.register(email.text, username.text, password.text)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = isTermsChecked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Register", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SocialLoginButton(iconResId = R.drawable.google_logo)
            SocialLoginButton(iconResId = R.drawable.facebook_logo)
            SocialLoginButton(iconResId = R.drawable.apple_logo)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Link
        Row {
            Text("Don't have an account?", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Sign In",
                fontSize = 14.sp,
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    // Navigate to Login Screen
                    navController.navigateUp() // or appropriate navigation action
                }
            )
        }
    }
}





