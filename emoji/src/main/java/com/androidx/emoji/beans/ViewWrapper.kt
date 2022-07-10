package com.androidx.emoji.beans

import android.view.View

class ViewWrapper(val target: View) {

    companion object {
        val WIDTH = "width"
        val HEIGHT = "height"
    }

    fun getWidth(): Int {
        return target.layoutParams.width
    }

    fun setWidth(width: Int) {
        target.layoutParams.width = width
    }

    fun getHeight(): Int {
        return target.layoutParams.height
    }

    fun setHeight(height: Int) {
        target.layoutParams.height = height
        target.requestLayout()
    }
}