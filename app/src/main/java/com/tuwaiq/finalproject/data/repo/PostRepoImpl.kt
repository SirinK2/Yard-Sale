package com.tuwaiq.finalproject.data.repo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.tuwaiq.finalproject.data.remote.dto.CurrentLocation
import com.tuwaiq.finalproject.domain.model.Post
import com.tuwaiq.finalproject.domain.repo.PostRepo
import com.tuwaiq.finalproject.util.Constant.postCollectionRef
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.*

private const val TAG = "PostRepoImpl"

class PostRepoImpl : PostRepo {


    override suspend fun addPost(post: Post) {

        val ref = postCollectionRef.document()
        post.id = ref.id
        ref.set(post)

    }


    override suspend fun savePost(
        context: Context,
        category: String,
        title: String,
        description: String,
        price: String,
        photoUrl:List<String>
    ) {

        val owner = Firebase.auth.currentUser?.uid.toString()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val location = fusedLocationClient.lastLocation.await()

        val myLocation = CurrentLocation(location?.latitude, location?.longitude)

        Log.d(TAG, " from location ${location?.longitude}  ${location?.latitude} ")


        val items = Post(owner,category, title, description, price, myLocation,photoUrl)

        addPost(items)

    }


    override suspend fun uploadImage(uri: List<Uri>): List<String>{
        val fileName = "${UUID.randomUUID()}.jpg"
        val imgRef = Firebase.storage.reference.child("images/").child(fileName)


            val uriList: MutableList<String> = mutableListOf()
            uri.forEach {
                val uriTask= imgRef.putFile(it).continueWithTask { task ->
                    if (!task.isSuccessful){
                        task.exception?.let { e ->
                            throw e
                        }
                    }
                     imgRef.downloadUrl



                }.await()
                uriList += uriTask.toString()



        }

        return uriList


    }

    
    @SuppressLint("MissingPermission")
    override suspend fun getPost(
        @ApplicationContext context: Context,
        dist: Float): List<Post> {
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedLocationClient.lastLocation.await()

        val listPost: MutableList<Post> = mutableListOf()
        val toLocation = Location("ToLocation")

            postCollectionRef.get().await().documents.forEach {



                val post = it.toObject(Post::class.java)

                post?.let { p ->


                    toLocation.apply {
                        this.longitude = p.location.longitude ?: 0.0
                        this.latitude = p.location.latitude ?: 0.0
                    }

                    Log.d(TAG, "getLocation: long ${p.location.longitude} lat ${p.location.latitude}  ")
                    val distance = location.distanceTo(toLocation)
                    Log.d(TAG, "dist: $distance")
                    if (distance <= dist) {
                        listPost += p
                        Log.d(TAG, "getLocation: distance $distance")
                    }
                }
            }

        return listPost

    }

    override suspend fun getMyPost():List<Post>{
        val listPost: MutableList<Post> = mutableListOf()
        val owner = Firebase.auth.currentUser?.uid.toString()
        val x =  postCollectionRef.whereEqualTo("owner", owner ).get().await().documents
        x.forEach {
           val post =  it.toObject(Post::class.java)
            post?.let { post->
                listPost += post
            }

        }
        return listPost
    }




}