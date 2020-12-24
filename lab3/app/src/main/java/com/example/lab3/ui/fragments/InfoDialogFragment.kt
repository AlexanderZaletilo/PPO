package com.example.lab3.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.lab3.R


class InfoDialogFragment(private val string_id: Int) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(string_id)
            .setPositiveButton(
                R.string.yes,
                DialogInterface.OnClickListener { _, _ ->
                })
        return builder.create()
    }
}