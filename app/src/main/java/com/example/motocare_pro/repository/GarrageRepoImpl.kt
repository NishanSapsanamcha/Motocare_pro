package com.example.motocare_pro.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.motocare_pro.model.GarrageModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class GarrageRepoImpl : GarrageRepo {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dwdbuyaec",
            "api_key" to "464792873614846",
            "api_secret" to "655Az-RJQFsVDWEhzmd7DiDGTvI"
        )
    )

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    val ref: DatabaseReference = database.getReference("garrages")
    override fun addGarrage(
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id = ref.push().key.toString()
        model.garrageId = id

        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Garrage added successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun deleteGarrage(
        garrageId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(garrageId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Garrage deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun editGarrage(
        garrageId: String,
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(garrageId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Garrage updated successfully")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun getAllGarrage(callback: (Boolean, String, List<GarrageModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allGarrages = mutableListOf<GarrageModel>()
                    for (data in snapshot.children) {
                        val garrages = data.getValue(GarrageModel::class.java)
                        if (garrages != null) {
                            allGarrages.add(garrages)
                        }
                    }
                    callback(true, "Garrages fetched successfully", allGarrages)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }

        })
    }

    override fun getGarrageById(
        garrageId: String,
        callback: (Boolean, String, GarrageModel?) -> Unit
    ) {
        ref.child(garrageId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val garrages = snapshot.getValue(GarrageModel::class.java)
                    if (garrages != null) {
                        callback(true, "Garrage fetched successfully", garrages)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromURI(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }
    override fun getFileNameFromURI(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}