package com.example.motocare_pro

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motocare_pro.ui.theme.Black
import com.example.motocare_pro.ui.theme.White
import com.example.motocare_pro.R
import com.example.motocare_pro.repository.UserRepoImpl
import com.example.motocare_pro.ui.theme.MotoGrey
import com.example.motocare_pro.ui.theme.MotoNavy
import com.example.motocare_pro.ui.theme.MotoOrange
import com.example.motocare_pro.viewmodel.userViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterBody()
        }
    }
}

// --- Password strength helper ---
fun isStrongPassword(password: String): Boolean {
    // At least 8 chars, 1 upper, 1 lower, 1 digit, 1 special character
    val pattern =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&^#_+=-]).{8,}$")
    return pattern.matches(password)
}

@Composable
fun RegisterBody() {

    var UserViewModel = remember { userViewModel(UserRepoImpl()) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var terms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    // 🔥 Firebase instances
    val auth = remember { FirebaseAuth.getInstance() }

    val rtdb = remember { FirebaseDatabase.getInstance().reference }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MotoNavy)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // Logo on top
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MotoCare Pro Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card containing registration form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MotoGrey
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text(
                        "Create Account",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = MotoOrange,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Join MotoCare Pro to manage your bike smartly",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp),
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = Black.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    )

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        shape = RoundedCornerShape(15.dp),
                        placeholder = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MotoOrange,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(15.dp),
                        placeholder = { Text("johndoe@gmail.com") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MotoOrange,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        shape = RoundedCornerShape(15.dp),
                        placeholder = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MotoOrange,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError =
                                if (password.isNotEmpty() && !isStrongPassword(password)) {
                                    "Min 8 chars, include upper, lower, number & special character"
                                } else {
                                    null
                                }
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = if (passwordVisible)
                                        painterResource(R.drawable.baseline_visibility_24)
                                    else
                                        painterResource(R.drawable.baseline_visibility_off_24),
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        isError = passwordError != null,
                        shape = RoundedCornerShape(15.dp),
                        placeholder = { Text("Strong Password") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MotoOrange,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        supportingText = {
                            if (passwordError != null) {
                                Text(
                                    text = passwordError!!,
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Terms & conditions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = terms,
                            onCheckedChange = { terms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MotoOrange,
                                checkmarkColor = White
                            )
                        )
                        Text("I agree to terms & conditions")
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // 🔥 Register button using Firebase
                    Button(
                        onClick = {
                            when {
                                fullName.isBlank() || email.isBlank() ||
                                        phone.isBlank() || password.isBlank() -> {
                                    Toast.makeText(
                                        context,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                !terms -> {
                                    Toast.makeText(
                                        context,
                                        "Please agree to terms and conditions",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                !isStrongPassword(password) -> {
                                    Toast.makeText(
                                        context,
                                        "Password is not strong enough",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    val cleanEmail = email.trim()

                                    auth.createUserWithEmailAndPassword(cleanEmail, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val uid = task.result.user?.uid
                                                if (uid != null) {
                                                    // Save extra profile fields to Firestore
                                                    val userData = hashMapOf(
                                                        "fullName" to fullName,
                                                        "email" to cleanEmail,
                                                        "phone" to phone,
                                                        "createdAt" to System.currentTimeMillis()
                                                    )
                                                        rtdb.child("users").child(uid).setValue(userData)
                                                            .addOnSuccessListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Registration Success",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            activity.finish() // back to login
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                context,
                                                                "Saved user but failed to store profile: ${e.localizedMessage}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Registration Success, but no user ID found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    activity.finish()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    task.exception?.localizedMessage
                                                        ?: "Registration failed",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoOrange,
                            contentColor = White
                        )
                    ) {
                        Text("Register")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Already a member? Sign In"
            Text(
                buildAnnotatedString {
                    append("Already a member? ")
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Sign In")
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        // back to LoginActivity
                        activity.finish()
                    },
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterBody()
}
