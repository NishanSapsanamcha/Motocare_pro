package com.example.motocare_pro.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

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
        setContent {
            GarrageScreen()
        }
    }
}