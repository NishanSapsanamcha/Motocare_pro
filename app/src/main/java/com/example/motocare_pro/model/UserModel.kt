package com.example.motocare_pro.model

data class UserModel(
    val userId:String = "",
    val email:String = "",
    val password:String = "",
    val fullName: String = "",
    val phoneNumber: Int = 0
){
    fun toMap () : Map<String,Any?>{
        return mapOf(
            "userId" to userId,
            "email" to email,
            "password" to password,
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
        )
    }
}