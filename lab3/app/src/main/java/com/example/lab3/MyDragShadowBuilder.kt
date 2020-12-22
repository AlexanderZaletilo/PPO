package com.example.lab3

import android.graphics.Canvas
import android.view.View


class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    override fun onDrawShadow(canvas: Canvas) {
        canvas.rotate(view.rotation)
        super.onDrawShadow(canvas)
    }
}