package com.androidx.emoji.beans

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmojiBean(
    val category: String,
    val char: String,
    val codes: String,
    val group: String,
    val name: String,
    val subgroup: String,
)