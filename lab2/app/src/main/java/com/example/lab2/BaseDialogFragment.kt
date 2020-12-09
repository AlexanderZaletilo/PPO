package com.example.lab2

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

open class BaseDialogFragment: DialogFragment()
{
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        dialog!!.show()
        val message = dialog!!.findViewById<TextView>(android.R.id.message)
        val preference = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val fontId = when (preference.getString("font", "medium")) {
            "small" -> R.style.FontStyle_Small
            "large" -> R.style.FontStyle_Large
            else -> R.style.FontStyle_Medium
        }
        val attrs = requireContext().obtainStyledAttributes(fontId, intArrayOf(R.attr.font_small))
        val colors = requireActivity().theme.obtainStyledAttributes(intArrayOf(R.attr.colorFirst, R.attr.colorSecond))
        message!!.textSize = attrs.getDimension(0, 18F)
        message!!.setTextColor(colors.getColor(0, Color.BLACK))
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(colors.getColor(1, Color.WHITE)))
        return view
    }
}