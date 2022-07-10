package com.androidx.emoji.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.preference.PreferenceManager
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.androidx.emoji.R

object KeyboardUtils {
    private val EXTRA_DEF_KEYBOARD_HEIGHT = "DEF_KEYBOARDHEIGHT"
    private var sDefKeyboardHeight = -1

    fun getDefKeyboardHeight(context: Context): Int {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = context.resources.getDimensionPixelSize(R.dimen.def_keyboard_height)
        }
        val height = PreferenceManager.getDefaultSharedPreferences(context).getInt(EXTRA_DEF_KEYBOARD_HEIGHT, 0)
        return if (height > 0 && sDefKeyboardHeight != height) height else sDefKeyboardHeight.also {
            sDefKeyboardHeight = it
        }
    }

    fun setDefKeyboardHeight(context: Context, height: Int) {
        if (sDefKeyboardHeight != height) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(EXTRA_DEF_KEYBOARD_HEIGHT, height).apply()
            sDefKeyboardHeight = height
        }
    }

    fun isFullScreen(context: Context): Boolean {
        if (context is Activity) {
            return isFullScreen(context.window)
        } else if (context is ContextThemeWrapper) {
            val baseContext = (context as ContextWrapper).baseContext
            if (baseContext is Activity) {
                return isFullScreen(baseContext.window)
            }
        }
        return false
    }

    /**
     * 是否全屏
     *
     * @param window
     * @return
     */
    fun isFullScreen(window: Window): Boolean {
        return window.attributes.flags and
                WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    /**
     * 开启软键盘
     *
     * @param et
     */
    fun openSoftKeyboard(et: EditText) {
        et.isFocusable = true
        et.isFocusableInTouchMode = true
        et.requestFocus()
        val inputManager = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(et, 0)
    }


    fun closeSoftKeyboardCompat(window: Window, editText: EditText) {
        if (isFullScreen(window)) {
            closeSoftKeyboard(editText)
        } else {
            closeSoftKeyboard(window)
        }
    }

    /**
     * 关闭软键盘
     *
     * @param window
     */
    fun closeSoftKeyboard(window: Window) {
        if (window.currentFocus == null) {
            return
        }
        try {
            val view = window.currentFocus
            val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            view?.clearFocus()
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭软键盘
     * 当使用全屏主题的时候,XhsEmoticonsKeyBoard屏蔽了焦点.关闭软键盘时,直接指定 closeSoftKeyboard(EditView)
     *
     * @param view
     */
    fun closeSoftKeyboard(view: View) {
        if (view.windowToken == null) {
            return
        }
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}