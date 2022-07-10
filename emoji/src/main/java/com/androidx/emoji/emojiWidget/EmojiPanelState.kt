package com.androidx.emoji.emojiWidget

import com.androidx.emoji.beans.EmojiBean

data class EmojiPanelState(val isLoading:Boolean,val emojiData:Map<String, List<EmojiBean>>)