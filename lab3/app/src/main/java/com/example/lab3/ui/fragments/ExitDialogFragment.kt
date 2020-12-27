package com.example.lab3.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lab3.R

class ExitDialogFragment(private val listener: ExitDialogListener) : DialogFragment() {
    interface ExitDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(R.string.exit_game)
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogNegativeClick(this)
                    })
            builder.create()
        return builder.create()
    }
}