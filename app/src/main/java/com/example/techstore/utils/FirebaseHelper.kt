package com.example.techstore.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val usersRef by lazy {
        database.getReference("users")
    }

    val productsRef by lazy {
        database.getReference("products")
    }

    val rentalsRef by lazy {
        database.getReference("rentals")
    }

    val repairsRef by lazy {
        database.getReference("repairs")
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun isAdmin(callback: (Boolean) -> Unit) {
        val userId = getCurrentUserId() ?: return callback(false)
        usersRef.child(userId).child("role").get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.getValue(String::class.java) == "admin")
            }
            .addOnFailureListener { callback(false) }
    }
}