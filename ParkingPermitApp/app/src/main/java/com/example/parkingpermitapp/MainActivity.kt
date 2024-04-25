package com.example.parkingpermitapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parkingpermitapp.ui.theme.ParkingPermitAppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.parkingpermitapp.cameraview.AppFunctions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import java.security.MessageDigest
import com.example.parkingpermitapp.ui.theme.PennStatePlatesPrimaryColor



class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            // Explain to the user that the feature is unavailable because the
            // features require a permission that the user has denied.
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkingPermitAppTheme{
                Surface(modifier = Modifier.fillMaxSize(),color = MaterialTheme.colorScheme.background) {
                    SignInScreen { user, password ->
                        if (user == "Alan" && password == "bc5300a645ed994e494e70e31fd11b91eb685ca139a1d50eab1e447d61da2be2") {
                            setContent {
                                HomeScreen()
                                requestCameraPermission()
                            }
                        } else {
                            // Handle incorrect credentials here (show error message, etc.)
                            Toast.makeText(this@MainActivity, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                // Permission is not granted; request it
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
@Composable
fun HomeScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp)
        .background(Color.White)) {
        Image(
            painter = painterResource(id = R.drawable.ps_transportations_services2),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        AppFunctions()


    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(onSignInClicked: (username: String, password: String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF1155CC)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image
        Image(
            painter = painterResource(id = R.drawable.ps_transportations_services2), // replace "your_image" with your image resource
            contentDescription = "Image",
            modifier = Modifier.size(400.dp),
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(16.dp))

        //User Name
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = PennStatePlatesPrimaryColor,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },

            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = PennStatePlatesPrimaryColor,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false
            ),
            trailingIcon = {
                TextButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                ) {
                    Text(if (passwordVisibility) "Hide"  else "Show")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign in button
        Button(
            onClick = { onSignInClicked(username, hashPassword(password)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }
    }
}

fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}







