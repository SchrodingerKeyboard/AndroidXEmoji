package com.androidx.emoji.keyboardWidget

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.animation.addListener
import androidx.core.util.keyIterator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidx.emoji.beans.PanelStatus
import com.androidx.emoji.beans.ViewWrapper
import com.androidx.emoji.utils.KeyboardUtils

class KeyboardPanelLayout : RelativeLayout {

    companion object {
        val DEFAULT_KEY = Int.MIN_VALUE
        val SINGLE_KEY = 0
    }

    private var panelStatus = PanelStatus()
    private val _panelStatusLiveData = MutableLiveData<PanelStatus>(panelStatus)
    val panelStatusLiveData: LiveData<PanelStatus> = _panelStatusLiveData

    private fun postValue(status: PanelStatus) {
        val newStatus = status.copy(isShowing = visibility != View.GONE)
        if (newStatus == _panelStatusLiveData.value) {
            return
        }
        panelStatus = status
        _panelStatusLiveData.postValue(newStatus)
    }

    private val mFuncViewArrayMap = SparseArray<View>()
    private var mCurrentFuncKey = DEFAULT_KEY

    private var mHeight = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        Log.d("KeyboardPanelLayout", "constructor1")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0) {
        Log.d("KeyboardPanelLayout", "constructor2")
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        Log.d("KeyboardPanelLayout", "constructor3")
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        Log.d("KeyboardPanelLayout", "addView:${child.id}")
        mFuncViewArrayMap.put(child.id, child)
        super.addView(child, index, params)
        child.visibility = GONE
        val viewids = mutableListOf<Int>()
        mFuncViewArrayMap.keyIterator().forEach {
            viewids.add(it)
        }
        postValue(panelStatus.copy(viewIds = viewids, height = mHeight))
    }

    fun hideAllFuncView() {
        if (panelStatus.isAniming) {
            return
        }

        postValue(panelStatus.copy(isAniming = true))
        ObjectAnimator.ofInt(ViewWrapper(this), ViewWrapper.HEIGHT, this.height, 0).apply {
            duration = 150
            interpolator = LinearInterpolator()
            addListener(onStart = {
                KeyboardUtils.closeSoftKeyboard(this@KeyboardPanelLayout)
            }, onEnd = {
                postValue(panelStatus.copy(isAniming = false))
                this@KeyboardPanelLayout.visibility = View.GONE
                for (i in 0 until mFuncViewArrayMap.size()) {
                    val keyTemp = mFuncViewArrayMap.keyAt(i)
                    mFuncViewArrayMap[keyTemp].visibility = GONE
                }
            })
        }.start()
        mCurrentFuncKey = DEFAULT_KEY
    }

    fun togglePanelView(key: Int, isKeyboardShow: Boolean, editText: EditText) {
        val context = context
        if (context is Activity) {
            togglePanelView((context).window, key, isKeyboardShow, editText)
        } else if (context is ContextThemeWrapper) {
            val baseContext = (context).baseContext
            if (baseContext is Activity) {
                togglePanelView(baseContext.window, key, isKeyboardShow, editText)
            }
        }
    }

    fun togglePanelView(window: Window, key: Int, isKeyboardShow: Boolean, editText: EditText) {
        if (visibility == VISIBLE && getCurrentFuncKey() == key) {
            if (isKeyboardShow) {
                KeyboardUtils.closeSoftKeyboardCompat(window, editText)
            } else {
                KeyboardUtils.openSoftKeyboard(editText)
            }
            postValue(panelStatus.copy(isShowing = true, isKeyboardShow = !isKeyboardShow))
        } else {
            if (isKeyboardShow) {
                KeyboardUtils.closeSoftKeyboardCompat(window, editText)
            }
            showFuncView(key, !isKeyboardShow)
        }

    }

    private fun showFuncView(key: Int, isKeyboardShow: Boolean) {
        if (mFuncViewArrayMap[key] == null) {
            return
        }
        for (i in 0 until mFuncViewArrayMap.size()) {
            val keyTemp = mFuncViewArrayMap.keyAt(i)
            if (keyTemp == key) {
                mFuncViewArrayMap[keyTemp].visibility = VISIBLE
            } else {
                mFuncViewArrayMap[keyTemp].visibility = GONE
            }
        }
        mCurrentFuncKey = key
        visibility = VISIBLE
        callPanelChange(key, isKeyboardShow)
    }

    fun callPanelChange(viewId: Int, isKeyboardShow: Boolean) {
        postValue(panelStatus.copy(showViewId = viewId, isKeyboardShow = isKeyboardShow))
    }

    fun getCurrentFuncKey(): Int {
        return mCurrentFuncKey
    }

    fun updateHeight(height: Int) {
        Log.d("KeyboardPanelLayout", "updateHeight:$height")
        mHeight = height
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        val params = layoutParams
        if (VISIBLE == visibility) {
            params.height = mHeight
            postValue(panelStatus.copy(isShowing = true, height = mHeight))
        } else if (INVISIBLE == visibility) {
            params.height = mHeight
            postValue(panelStatus.copy(isShowing = false, height = mHeight))
        } else {
            params.height = 0
            postValue(panelStatus.copy(isShowing = false, height = 0))
        }
        layoutParams = params
    }

    fun isOnlyShowSoftKeyboard(): Boolean {
        return mCurrentFuncKey == DEFAULT_KEY
    }
}