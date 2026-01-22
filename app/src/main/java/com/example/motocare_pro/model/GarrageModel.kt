package com.example.motocare_pro.model

data class GarrageModel(
    var garrageId: String = "",
    var name: String = "",
    var location: String = "",
    var contact: String = "",
    var image: String = "",
) {
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "garrageId" to garrageId,
            "name" to name,
            "location" to location,
            "contact" to contact,
            "image" to image
        )
    }
}