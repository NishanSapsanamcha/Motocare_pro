package com.example.motocare_pro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.motocare_pro.model.UserModel
import com.example.motocare_pro.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

class userViewModel(val repo: UserRepo): ViewModel() {

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.login(email,password,callback)
    }

    fun register(
        email: String,
        password: String,
        callback:(Boolean, String, String) -> Unit
    ){
        repo.register(email,password,callback)
    }

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.addUserToDatabase(userId, model, callback)
    }

    fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.updateProfile(userId, model, callback)
    }

    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.deleteAccount(userId, callback)
    }

    private val _users = MutableLiveData<UserModel?>()
    val users : MutableLiveData<UserModel?> get() = _users
    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers : MutableLiveData<List<UserModel>?> get() = _allUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading : MutableLiveData<Boolean> get() = _loading


    fun getUserById(
        userId: String){
        _loading.postValue(true)
        repo.getUserById(userId){
            success,msg,data ->
            if(success){
                _loading.postValue(false)
                _users.postValue(data)
            } else {
                _loading.postValue(false)
                _users.postValue(null)
            }
        }
    }

    fun getAllUser(){
        _loading.postValue(true)
        repo.getAllUser {
            success, msg, data ->
            if(success){
                _loading.postValue(false)
                _allUsers.postValue(data)
            } else {
                _loading.postValue(false)
                _allUsers.postValue(emptyList())
            }
        }
    }

    fun getCurrentUser()
            : FirebaseUser?{
        return repo.getCurrentUser()
    }

    fun logOut(
        callback: (Boolean, String) -> Unit
    ){
        repo.logOut(callback)
    }

    fun forgetPassword(email: String,
                       callback: (Boolean, String) -> Unit){
        repo.forgetPassword(email, callback)
    }
}