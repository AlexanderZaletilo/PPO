package com.example.lab2

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

class SaveDialogFragment(private val listener: SaveDialogListener) : BaseDialogFragment() {
    interface SaveDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(R.string.changes_dialog)
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogNegativeClick(this)
                    })
                .setNeutralButton(R.string.cancel,
                    DialogInterface.OnClickListener {_, _ ->
                        listener.onDialogNeutralClick(this)
                    })
            builder.create()
        return builder.create()
    }
}