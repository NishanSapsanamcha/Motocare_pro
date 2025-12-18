package com.example.motocare_pro.repository

import com.example.motocare_pro.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl: UserRepo {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true,"Login success")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration Successful", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
            }
        }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        // var id = ref.push().key.toString() -- for adding product this will create product id
        ref.child(userId).setValue(model).addOnCompleteListener {
            if(it.isSuccessful) {
                callback(true, "Registration success")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful) {
                callback(true, "Profile Updated")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                callback(true, "Account Deleted")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val users = snapshot.getValue(UserModel:: class.java)
                        if (users != null) {
                            callback(true, "Profile fetched", users)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>?) -> Unit) {
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val allUsers = mutableListOf<UserModel>()
                    for (data in snapshot.children){
                        val user = data.getValue(UserModel::class.java)
                        if(user != null) {
                            allUsers.add(user)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logOut(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "logout Successfully")
        } catch (e: Exception) {
            callback(false, "${e.message}")
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true,"Email sent to $email")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }
}