package com.androidx.emoji.emojiWidget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.androidx.emoji.beans.EmojiBean
import com.androidx.emoji.databinding.EmojiPopupWindowLayoutBinding

class EmojiPopupWindow : PopupWindow {
    private var binding: EmojiPopupWindowLayoutBinding? = null

    constructor(context: Context) : super(context) {
        binding = EmojiPopupWindowLayoutBinding.inflate(LayoutInflater.from(context))
        contentView = binding?.root
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show(view: View, emoji: EmojiBean?) {

        setEmojiData(emoji)
        measure()

        val array = intArrayOf(0, 0)
        view.getLocationOnScreen(array)
        var x = array[0]
        var y = array[1]-measuredHeight()
        x -= (Math.abs(measuredWidth() - view.width) / 2.0).toInt()
        if(x <=0) x = 10
        if(x+measuredWidth() >= view.resources.displayMetrics.widthPixels) x = view.resources.displayMetrics.widthPixels-measuredWidth()-10


        showAtLocation(view, Gravity.NO_GRAVITY, x,y)
    }

    private fun measure() {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        contentView.measure(widthMeasureSpec, heightMeasureSpec)
        contentView.layout(0, 0, contentView.measuredWidth, contentView.measuredHeight)
    }

    private fun measuredHeight(): Int = contentView.measuredHeight
    private fun measuredWidth(): Int = contentView.measuredWidth

    override fun dismiss() {
        binding = null
        super.dismiss()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(contentView: View) : super(contentView)

    constructor(width: Int, height: Int) : super(width, height)

    constructor(contentView: View, width: Int, height: Int) : super(contentView, width, height)

    constructor(contentView: View, width: Int, height: Int, focusable: Boolean) : super(contentView, width, height, focusable)

    fun setEmojiData(emoji: EmojiBean?) {
        binding?.tvEmojiChar?.text = emoji?.char
        binding?.tvEmojiName?.text = emoji?.name
    }
}