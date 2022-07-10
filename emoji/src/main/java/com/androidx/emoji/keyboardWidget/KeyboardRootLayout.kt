package com.androidx.emoji.keyboardWidget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.androidx.emoji.utils.KeyboardUtils

class KeyboardRootLayout : AutoHeightLayout {
    private var mPanelLayout: KeyboardPanelLayout? = null
    private var mOnKeyboardStatusListener: OnKeyboardStatusListener? = null
    private var mDispatchKeyEventPreImeLock = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        Log.d("KeyboardRootLayout", "constructor1")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0) {
        Log.d("KeyboardRootLayout", "constructor2")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        Log.d("KeyboardRootLayout", "constructor3")
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (mPanelLayout != null) {
            return
        }
        mPanelLayout = findSuitablePanelLayout(child)
    }

    private fun findSuitablePanelLayout(child: View): KeyboardPanelLayout? {
        if (child is KeyboardPanelLayout) {
            return child
        } else {
            //递归寻找FuncLayout
            if (child is ViewGroup) {
                val childCount = child.childCount
                for (i in childCount - 1 downTo 0) {
                    val childAt = child.getChildAt(i)
                    val childFuncLayout = findSuitablePanelLayout(childAt)
                    if (childFuncLayout != null) {
                        return childFuncLayout
                    }
                }
            }
        }
        return null
    }

    override fun onSoftKeyboardHeightChanged(height: Int) {
        mPanelLayout?.updateHeight(height)
    }

    override fun onKeyboardShow(keyboardHeight: Int) {
        super.onKeyboardShow(keyboardHeight)

        mOnKeyboardStatusListener?.onKeyboardStatus(true, keyboardHeight)

        mPanelLayout?.apply {
            visibility = INVISIBLE
            callPanelChange(KeyboardPanelLayout.DEFAULT_KEY,true)
        }
    }

    override fun onKeyboardClose() {
        super.onKeyboardClose()
        mOnKeyboardStatusListener?.onKeyboardStatus(false, 0)
        mPanelLayout?.run {
            if (isOnlyShowSoftKeyboard()) {
                reset()
            } else {
                if (visibility == INVISIBLE) {
                    visibility = GONE
                } else {
                    callPanelChange(getCurrentFuncKey(),false)
                }
            }
        }
    }

    private fun reset() {
        KeyboardUtils.closeSoftKeyboard(this)
        mPanelLayout?.hideAllFuncView()
    }

    fun setOnKeyboardStatusListener(listener: OnKeyboardStatusListener?) {
        mOnKeyboardStatusListener = listener
    }

    override fun setFitsSystemWindows(fitSystemWindows: Boolean) {
        if (fitSystemWindows && parent != null) {
            (parent as View).fitsSystemWindows = true
        } else {
            super.setFitsSystemWindows(fitSystemWindows)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false
                    return true
                }
                return if (mPanelLayout?.isShown == true) {
                    reset()
                    true
                } else {
                    super.dispatchKeyEvent(event)
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return if (KeyboardUtils.isFullScreen(context)) {
            false
        } else super.requestFocus(direction, previouslyFocusedRect)
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        if (KeyboardUtils.isFullScreen(context)) {
            return
        }
        super.requestChildFocus(child, focused)
    }

    /**
     * 供全屏Activity调用
     *
     * @param event
     * @return
     */
    fun dispatchKeyEventInFullScreen(event: KeyEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.keyCode) {
            KeyEvent.KEYCODE_BACK -> if (KeyboardUtils.isFullScreen(context) && (mPanelLayout?.isShown == true)) {
                reset()
                return true
            }
        }
        return false
    }
}

interface OnKeyboardStatusListener {
    fun onKeyboardStatus(isShowing: Boolean, height: Int)
}