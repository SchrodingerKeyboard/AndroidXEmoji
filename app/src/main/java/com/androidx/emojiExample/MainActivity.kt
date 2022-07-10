package com.androidx.emojiExample

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.androidx.emoji.emojiWidget.EmojiPanelFragment
import com.androidx.emoji.keyboardWidget.OnKeyboardStatusListener
import com.androidx.emoji.utils.KeyboardUtils
import com.androidx.emojiExample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.tvEmoji?.movementMethod = LinkMovementMethod.getInstance()
        val fragment = supportFragmentManager.findFragmentByTag("emojiPanelFragment") as EmojiPanelFragment

        fragment.onEmojiSelectedListener = {
            binding?.editText?.apply {
                text.insert(selectionStart, it.char)
            }
        }

        fragment.onEmojiDeleteListener = {
            binding?.editText?.apply {
                if (selectionStart <= 0) return@apply
                onKeyDown(KeyEvent.KEYCODE_DEL, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        }
        initListeners()
    }

    private fun initListeners() {
        binding?.keyboardPanelLayout?.panelStatusLiveData?.observe(this) { panelStatus ->
            Log.d("panelStatus", "panelStatus:$panelStatus")
            if (!panelStatus.isAniming
                && panelStatus.isShowing
                && !panelStatus.isKeyboardShow
                && panelStatus.showViewId == R.id.panel_emoji
            ) {
                binding?.editText?.requestFocus()
            }
        }

        binding?.keyboardRootLayout?.setOnKeyboardStatusListener(object : OnKeyboardStatusListener {
            override fun onKeyboardStatus(isShowing: Boolean, height: Int) {
                Log.d("keyboard", "onKeyboardStatus isShowing:$isShowing\theight:$height")
            }
        })

        binding?.btnEmoji?.setOnClickListener {
            if (binding?.editText == null) {
                return@setOnClickListener
            }
            binding?.keyboardPanelLayout?.togglePanelView(
                R.id.panel_emoji,
                binding?.keyboardRootLayout?.isKeyboardShow() ?: false, binding?.editText!!
            )
        }

        binding?.btnFunc?.setOnClickListener {
            if (binding?.editText == null) {
                return@setOnClickListener
            }
            binding?.keyboardPanelLayout?.togglePanelView(
                R.id.func_emoji,
                binding?.keyboardRootLayout?.isKeyboardShow() ?: false, binding?.editText!!
            )
        }

        binding?.btnVoice?.setOnClickListener {
            if (binding?.editText == null) {
                return@setOnClickListener
            }
            binding?.keyboardPanelLayout?.hideAllFuncView()
            loadEmoji()
        }

        binding?.btnSend?.setOnClickListener {
            binding?.editText?.apply {
                if (text.isEmpty()) {
                    return@setOnClickListener
                }
                binding?.tvEmoji?.append("$text\n")
                text = null
            }
        }
    }

    private fun loadEmoji() {

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (KeyboardUtils.isFullScreen(this)
            && (binding?.keyboardRootLayout?.dispatchKeyEventInFullScreen(event) == true)
        ) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}