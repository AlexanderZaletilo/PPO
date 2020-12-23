package com.example.lab3.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.lab3.R


class ProfileDialogFragment(val image: Bitmap, val name: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity()).setMessage(name)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_profile_dialog, null)
        val imageView = view.findViewById<ImageView>(R.id.game_opponent_image)
        imageView.setImageBitmap(image)
        builder.setView(view)
        return builder.create()
    }
}