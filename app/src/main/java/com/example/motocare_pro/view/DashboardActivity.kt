package com.example.motocare_pro.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motocare_pro.ui.theme.Black
import com.example.motocare_pro.ui.theme.MotoGrey
import com.example.motocare_pro.ui.theme.MotoNavy
import com.example.motocare_pro.ui.theme.MotoOrange
import com.example.motocare_pro.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*
import com.example.motocare_pro.R
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DashboardUI() }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardUI() {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    var fullName by remember { mutableStateOf("User") }

    // Load name (Firestore first, fallback to displayName/email)
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user == null) {
            fullName = "User"
            return@LaunchedEffect
        }

        // 1) Try Firestore: users/{uid}.fullName
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val nameFromDb = doc.getString("fullName")
                fullName = when {
                    !nameFromDb.isNullOrBlank() -> nameFromDb
                    !user.displayName.isNullOrBlank() -> user.displayName!!
                    !user.email.isNullOrBlank() -> user.email!!.substringBefore("@")
                    else -> "User"
                }
            }
            .addOnFailureListener {
                // fallback
                fullName = when {
                    !user.displayName.isNullOrBlank() -> user.displayName!!
                    !user.email.isNullOrBlank() -> user.email!!.substringBefore("@")
                    else -> "User"
                }
            }
    }

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

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MotoCare Pro Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MotoGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text(
                        "Dashboard",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = MotoOrange,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Full name will be shown in dashboard
                    Text(
                        "Hello, $fullName",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp),
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = Black.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    )

                    Text(
                        "Welcome to Motocare Pro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Black.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { context.startActivity(Intent(context, AppointmentActivity::class.java)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoOrange,
                            contentColor = White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text("Book Appointment")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { context.startActivity(Intent(context, GaragesActivity::class.java)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Garages")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Profile")
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                            activity.finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = White
                        )
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}