package com.tuwaiq.finalproject.data.repo

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.finalproject.domain.repo.UserRepo
import com.tuwaiq.finalproject.domain.model.User
import kotlinx.coroutines.tasks.await
import java.lang.Exception

private const val TAG = "UserRepoImpl"

class UserRepoImpl: UserRepo {

    private val userCollectionRef = Firebase.firestore.collection("users")

    override fun saveUser(user: User) { userCollectionRef.add(user) }

    override suspend fun getUser():List<User> {
       return userCollectionRef.get().await().toObjects(User::class.java)
    }


}