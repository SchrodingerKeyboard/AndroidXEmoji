package com.androidx.emoji.keyboardWidget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout

open class SoftKeyboardSizeWatchLayout : RelativeLayout {

    private var mOldHeight = -1
    private var mNowHeight = -1
    protected var mWindowHeight = 0
    private var mKeyboardShow = false

    protected var mRootView: View? = null

    constructor (context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        Log.d("SoftKeyboardSize", "constructor1")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0) {
        Log.d("SoftKeyboardSize", "constructor2")
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        Log.d("SoftKeyboardSize", "constructor3")
    }

    init {
        initHeight()
    }

    private fun initHeight() {
        Log.d("SoftKeyboardSize", "initHeight")
        viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            if (mRootView == null) {
                mRootView = findRootLayout()
            }

            mRootView?.getWindowVisibleDisplayFrame(r)

            if (mWindowHeight == 0) {
                mWindowHeight = r.bottom
            }
            mNowHeight = mWindowHeight - r.bottom
            if (mOldHeight != -1 && mNowHeight != mOldHeight) {
                if (mNowHeight > 0) {
                    mKeyboardShow = true
                    onKeyboardShow(mNowHeight)
                } else {
                    mKeyboardShow = false
                    onKeyboardClose()
                }
            }
            mOldHeight = mNowHeight
        }
    }

    fun isKeyboardShow(): Boolean {
        return mKeyboardShow
    }


    protected open fun onKeyboardShow(keyboardHeight: Int) {}

    protected open fun onKeyboardClose() {}

    /**
     * @return window 根view
     */
    private fun findRootLayout(): View {
        var rootView: View = this
        while (rootView.parent != null && rootView.parent is View) {
            rootView = rootView.parent as View
        }
        return rootView
    }
}