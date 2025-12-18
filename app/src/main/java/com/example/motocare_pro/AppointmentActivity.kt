package com.example.motocare_pro

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motocare_pro.ui.theme.MotoGrey
import com.example.motocare_pro.ui.theme.MotoNavy
import com.example.motocare_pro.ui.theme.MotoOrange
import com.example.motocare_pro.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppointmentUI() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentUI() {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var vehicleNumber by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var appointmentDate by remember { mutableStateOf("") } // simple input like 2025-12-20
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Appointment") },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MotoNavy)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MotoGrey),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Fill appointment details",
                        fontWeight = FontWeight.Bold,
                        color = MotoOrange
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Vehicle Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = serviceType,
                        onValueChange = { serviceType = it },
                        label = { Text("Service Type (e.g., Oil Change)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = appointmentDate,
                        onValueChange = { appointmentDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val user = auth.currentUser
                            if (user == null) {
                                Toast.makeText(context, "Please login again", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (vehicleNumber.isBlank() || serviceType.isBlank() || appointmentDate.isBlank()) {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSaving = true

                            val data = hashMapOf(
                                "userId" to user.uid,
                                "vehicleNumber" to vehicleNumber.trim(),
                                "serviceType" to serviceType.trim(),
                                "appointmentDate" to appointmentDate.trim(),
                                "notes" to notes.trim(),
                                "status" to "pending",
                                "createdAt" to FieldValue.serverTimestamp()
                            )

                            db.collection("appointments")
                                .add(data)
                                .addOnSuccessListener {
                                    isSaving = false
                                    Toast.makeText(context, "Appointment booked!", Toast.LENGTH_SHORT).show()
                                    activity.finish()
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        },
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoOrange,
                            contentColor = White
                        )
                    ) {
                        Text(if (isSaving) "Saving..." else "Confirm Appointment")
                    }
                }
            }
        }
    }
}
