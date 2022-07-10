package com.androidx.emoji.keyboardWidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.androidx.emoji.utils.KeyboardUtils

abstract class AutoHeightLayout : SoftKeyboardSizeWatchLayout {

    protected var mMaxParentHeight = 0
    protected var mSoftKeyboardHeight = 0
    protected var mConfigurationChangedFlag = false
    private var maxParentHeightChangeListener: OnMaxParentHeightChangeListener? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        Log.d("AutoHeightLayout", "constructor1")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0) {
        Log.d("AutoHeightLayout", "constructor2")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        Log.d("AutoHeightLayout", "constructor3")
    }

    init {
        mSoftKeyboardHeight = KeyboardUtils.getDefKeyboardHeight(context)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        check(childCount <= 1) { "can host only one direct child" }
        super.addView(child, index, params)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        onSoftKeyboardHeightChanged(mSoftKeyboardHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h
        }
    }

    fun updateMaxParentHeight(maxParentHeight: Int) {
        mMaxParentHeight = maxParentHeight
        maxParentHeightChangeListener?.onMaxParentHeightChange(maxParentHeight)
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mConfigurationChangedFlag = true
        mWindowHeight = 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mConfigurationChangedFlag) {
            mConfigurationChangedFlag = false
            val r = Rect()
            mRootView?.getWindowVisibleDisplayFrame(r)
            if (mWindowHeight == 0) {
                mWindowHeight = r.bottom
            }
            val mNowh = mWindowHeight - r.bottom
            mMaxParentHeight = mNowh
        }
        if (mMaxParentHeight != 0) {
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode)
            super.onMeasure(widthMeasureSpec, expandSpec)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onKeyboardShow(keyboardHeight: Int) {
        if (mSoftKeyboardHeight != keyboardHeight) {
            mSoftKeyboardHeight = keyboardHeight
            KeyboardUtils.setDefKeyboardHeight(context, mSoftKeyboardHeight)
            onSoftKeyboardHeightChanged(mSoftKeyboardHeight)
        }
    }

    abstract fun onSoftKeyboardHeightChanged(height: Int)

    interface OnMaxParentHeightChangeListener {
        fun onMaxParentHeightChange(height: Int)
    }

    fun setOnMaxParentHeightChangeListener(listener: OnMaxParentHeightChangeListener?) {
        maxParentHeightChangeListener = listener
    }
}