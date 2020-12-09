package com.example.lab2

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager


class InfoDialogFragment(val string_id: Int) : BaseDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(string_id)
            .setPositiveButton(R.string.yes,
                DialogInterface.OnClickListener { _, _ ->
                })
        return builder.create()
    }
}