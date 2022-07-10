package com.androidx.emoji.emojiWidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.androidx.emoji.utils.readEmojiFromAssets
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EmojiPanelViewModel(context: Application) : AndroidViewModel(context) {

    val emojiPanelState: StateFlow<EmojiPanelState> = readEmojiFromAssets(context)
        .asFlow()
        .map {
            EmojiPanelState(false, it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = EmojiPanelState(true, emptyMap())
        )
}