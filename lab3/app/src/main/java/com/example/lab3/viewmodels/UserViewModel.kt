package com.example.lab3.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab3.game.Field
import com.example.lab3.game.Point
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class UserViewModel: ViewModel() {
    var imageUri =  MutableLiveData<Uri>()
    var imageBitmap = MutableLiveData<Bitmap>()
    var fetched = false
    val storage = Firebase.storage
    val auth = Firebase.auth
    val storageRef: StorageReference
    init {
        storageRef = storage.reference
    }
    fun updateName(name: String){
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        auth.currentUser!!.updateProfile(profileUpdates)
    }
    fun upload(bitmap: Bitmap)
    {
        imageBitmap.value = bitmap
        val uid = auth.currentUser!!.uid
        val ref = storageRef.child("images/$uid.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        ref.putBytes(data).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateImageUrl(task.result!!)
            }
        }
    }
    private fun updateImageUrl(uri: Uri)
    {
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        auth.currentUser!!.updateProfile(profileUpdates)
    }
    fun downloadImage()
    {
        val uid = auth.currentUser!!.uid
        val ref = storageRef.child("images/$uid.jpg")
        val ONE_MEGABYTE: Long = 1024 * 1024 * 10
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            imageBitmap.value = BitmapFactory.decodeStream(ByteArrayInputStream(it))
        }
    }
}