package com.example.motocare_pro.repository

import android.net.Uri
import com.example.motocare_pro.model.GarrageModel
import android.content.Context


interface GarrageRepo {
    fun addGarrage(
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteGarrage(garrageId: String, callback: (Boolean, String) -> Unit)

    fun editGarrage(
        garrageId: String,
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAllGarrage(callback: (Boolean, String, List<GarrageModel>?) -> Unit)

    fun getGarrageById(garrageId: String, callback: (Boolean, String, GarrageModel?) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromURI(context: Context,imageUri: Uri) : String?
}