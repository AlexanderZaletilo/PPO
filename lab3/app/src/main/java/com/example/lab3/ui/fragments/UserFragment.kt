package com.example.lab3.ui.fragments

import android.R.attr
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.lab3.*
import com.example.lab3.ui.activities.MainActivity
import com.example.lab3.viewmodels.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.tkeunebr.gravatar.Gravatar


class UserFragment : Fragment() {
    private lateinit var viewModel: UserViewModel
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val editImageButton = view.findViewById<Button>(R.id.user_editimage)
        val editNameButton = view.findViewById<Button>(R.id.user_editname_apply)
        val nameEditText = view.findViewById<EditText>(R.id.user_editname)
        val imageView = view.findViewById<ImageView>(R.id.user_image)
        val gravatarButton = view.findViewById<Button>(R.id.user_gravatar)
        val user = Firebase.auth.currentUser!!
        val profile = user.providerData[0]
        nameEditText.setText(profile.displayName)
        imageView.isDrawingCacheEnabled = true
        editNameButton.setOnClickListener {
            viewModel.updateName(nameEditText.text.toString())
        }
        if(!viewModel.fetched)
        {
            if (profile.photoUrl != null)
                viewModel.downloadImage()
            viewModel.fetched = true
        }
        gravatarButton.setOnClickListener {
           val url = "https://eu.ui-avatars.com/api/?name=${user.displayName}&size=240"
            setImage(Uri.parse(url), imageView)
        }
        viewModel.imageBitmap.observe(requireActivity()) {
            imageView.setImageBitmap(viewModel.imageBitmap.value!!)
            viewModel.imageBitmap.removeObservers(requireActivity())
        }
        viewModel.imageUri.observe(requireActivity()) {
            setImage(it, imageView)
        }
        editImageButton.setOnClickListener {
            (requireActivity() as MainActivity).requestImageFromGallery()
        }
        return view
    }
    private fun setImage(uri: Uri, imageView: ImageView)
    {
        Glide.with(requireContext())
            .load(uri)
            .into(object : ViewTarget<ImageView, Drawable>(imageView) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                    viewModel.upload((imageView.drawable as BitmapDrawable).bitmap)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Toast.makeText(requireContext(), "Error :(", Toast.LENGTH_LONG).show()
                }

            })
    }
}