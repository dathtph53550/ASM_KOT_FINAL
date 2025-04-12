package com.example.asm_1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asm_1.R

data class UserProfilee(
    var fullName: String,
    var birthDate: String,
    var gender: String,
    var phone: String,
    var email: String,
    var avatarRes: Int
)


val userProfile = UserProfilee(
    fullName = "Hoang Tien Dat",
    birthDate = "19/06/1999",
    gender = "Male",
    phone = "+1 325-433-7656",
    email = "hoangdat07082005@gmail.com",
    avatarRes = R.drawable.avatar
)

@Composable
fun SettingProfileScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf(userProfile.fullName) }
    var birthDate by remember { mutableStateOf(userProfile.birthDate) }
    var gender by remember { mutableStateOf(userProfile.gender) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var email by remember { mutableStateOf(userProfile.email) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(userProfile)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileInputField("Full Name", fullName) { fullName = it }
        ProfileInputField("Date of Birth", birthDate) { birthDate = it }
        GenderDropdown(gender) { gender = it }
        ProfileInputField("Phone", phone) { phone = it }
        ProfileInputField("Email", email) { email = it }
        SaveButton()
    }
}

@Composable
fun ProfileHeader(user: UserProfile) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Image(
            painter = painterResource(id = user.avatarRes),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Change Profile Picture",
            modifier = Modifier
                .size(24.dp)
                .background(Color.White, shape = CircleShape)
                .padding(4.dp)
        )
    }
}


@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun GenderDropdown(selectedGender: String, onGenderSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Other")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Gender", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedTextField(
                value = selectedGender,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genders.forEach { gender ->
                    DropdownMenuItem(onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }) {
                        Text(gender)
                    }
                }
            }
        }
    }
}

fun DropdownMenuItem(onClick: () -> Unit, interactionSource: @Composable () -> Unit) {

}


@Composable
fun SaveButton() {
    Button(
        onClick = { /* Lưu dữ liệu */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
        )
    ) {
        Text("Save", color = Color.White, fontSize = 16.sp)
    }
}
