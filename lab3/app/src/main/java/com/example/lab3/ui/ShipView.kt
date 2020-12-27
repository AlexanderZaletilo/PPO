package com.example.lab3.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.lab3.game.Ship


class ShipView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    public var length = 0
    public var isHorizontal = true
    public var associatedFieldShip: Ship? = null
}