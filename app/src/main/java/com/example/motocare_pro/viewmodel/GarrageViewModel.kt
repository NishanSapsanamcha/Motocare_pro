package com.example.motocare_pro.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.motocare_pro.model.GarrageModel
import com.example.motocare_pro.repository.GarrageRepo

class GarrageViewModel(val repo: GarrageRepo) : ViewModel() {
    fun addGarrage(
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addGarrage(model, callback)
    }

    fun deleteGarrage(garrageId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteGarrage(garrageId, callback)
    }

    fun editGarrage(
        garrageId: String,
        model: GarrageModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.editGarrage(garrageId, model, callback)
    }

    private val _garrages = MutableLiveData<GarrageModel?>()
    val garrages: MutableLiveData<GarrageModel?> get() = _garrages

    private val _allGarrages = MutableLiveData<List<GarrageModel>?>()
    val allGarrages: MutableLiveData<List<GarrageModel>?> get() = _allGarrages

    fun getAllGarrage() {
        repo.getAllGarrage { success, message, data ->
            if (success) {
                _allGarrages.value = data
            } else {
                _allGarrages.value = emptyList()
            }
        }
    }

    fun getGarrageById(garrageId: String) {
        repo.getGarrageById(garrageId) { success, message, data ->
            if (success) {
                _garrages.value = data
            } else {
                _garrages.value = null
            }
        }
    }
    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }
}