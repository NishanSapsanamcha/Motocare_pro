package com.example.motocare_pro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.motocare_pro.ui.theme.MotoGrey
import com.example.motocare_pro.ui.theme.MotoNavy
import com.example.motocare_pro.ui.theme.MotoOrange
import com.example.motocare_pro.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore

data class Garage(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val imageUrl: String = ""
)

class GaragesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GaragesUI() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaragesUI() {
    val context = LocalContext.current
    val activity = context as Activity
    val db = remember { FirebaseFirestore.getInstance() }

    var garages by remember { mutableStateOf<List<Garage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        db.collection("garages")
            .get()
            .addOnSuccessListener { snap ->
                garages = snap.documents.map { d ->
                    Garage(
                        id = d.id,
                        name = d.getString("name") ?: "",
                        address = d.getString("address") ?: "",
                        phone = d.getString("phone") ?: "",
                        imageUrl = d.getString("imageUrl") ?: ""
                    )
                }.filter { it.name.isNotBlank() }

                loading = false
            }
            .addOnFailureListener { e ->
                error = e.message
                loading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garages") },
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
                .padding(16.dp)
        ) {

            when {
                loading -> {
                    CircularProgressIndicator(color = MotoOrange)
                    Spacer(Modifier.height(8.dp))
                    Text("Loading garages...", color = MotoOrange)
                }

                error != null -> {
                    Text("Error: $error", color = MotoOrange)
                }

                garages.isEmpty() -> {
                    Text("No garages available", color = MotoOrange)
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(garages) { garage ->
                            GarageCard(garage)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GarageCard(garage: Garage) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MotoGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {

            if (garage.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = garage.imageUrl,
                    contentDescription = "Garage Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    garage.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Spacer(Modifier.height(6.dp))
                Text("📍 ${garage.address}")

                if (garage.phone.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text("📞 ${garage.phone}")
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, AppointmentActivity::class.java).apply {
                            putExtra("garageId", garage.id)
                            putExtra("garageName", garage.name)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoOrange,
                        contentColor = White
                    )
                ) {
                    Text("Book Appointment")
                }
            }
        }
    }
}
