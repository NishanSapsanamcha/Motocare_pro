package com.example.motocare_pro.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.motocare_pro.repository.GarrageRepoImpl
import com.example.motocare_pro.viewmodel.GarrageViewModel

@Composable
fun GarrageScreen() {

    val garrageViewModel = remember { GarrageViewModel(GarrageRepoImpl()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        garrageViewModel.getAllGarrage()
    }

    val allGarrages = garrageViewModel.allGarrages.observeAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddGarrageActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Garage")
            }
        }
    ) { paddingValues ->
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
            ) {
            item {
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        confirmButton = {
                            TextButton(onClick = {}) { Text("Update") }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog = false
                            }) { Text("Cancel") }
                        },
                        title = { Text("Update Garrage") },
                        text = {
                            Column {

                            }
                        }
                    )
                }
            }
            items(allGarrages.value!!.size) { index ->
                val data = allGarrages.value!![index]
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)) {

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(data.name)
                            Text(data.location)
                            Text(data.contact)

                        }
                        Column(
                            verticalArrangement = Arrangement.Top
                        ) {
                            IconButton(onClick = {
                                showDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                            }
                            IconButton(onClick = {
                                garrageViewModel.deleteGarrage(data.garrageId) { succes, msg ->
                                    if (succes) {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GarragePre() {
    GarrageScreen()
}