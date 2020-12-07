package com.example.lab2

import android.app.Dialog
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager


class ColorPaletteDialogFragment(private val listener: ColorDialogListener, var selected: Int ) : BaseDialogFragment() {
    lateinit var colors: List<List<ImageButton>>
    private lateinit var colorsResource: IntArray
    interface ColorDialogListener {
        fun onColorSelected(selected: Int)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity()).setMessage(R.string.select_color)
        colorsResource = resources.getIntArray(R.array.color_palette_arr)
        val inflater = requireActivity().layoutInflater
        val table: TableLayout = inflater.inflate(R.layout.color_dialog_layout, null) as TableLayout
        colors = table.children.map{ row -> (row as TableRow).children.map{ but -> but as ImageButton}.toList()}.toList()
        for (i in 0..2) {
            for (j in 0..2) {
                val color = colorsResource[i * 3 + j]
                if(color == selected)
                    colors[i][j].isSelected = true
                colors[i][j].background.setColorFilter(
                    color,
                    PorterDuff.Mode.SRC_OVER
                );
            }
        }
        builder.setView(table)
        val dialog = builder.create()
        for (i in 0..2) {
            for (j in 0..2) {
                colors[i][j].setOnClickListener {
                    selected = colorsResource[i * 3 + j]
                    listener.onColorSelected(selected)
                    dialog.dismiss()
                }
            }
        }
        return dialog
    }
}