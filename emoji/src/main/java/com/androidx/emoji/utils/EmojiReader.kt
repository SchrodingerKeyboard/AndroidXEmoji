package com.androidx.emoji.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidx.emoji.beans.EmojiBean
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import java.io.BufferedReader
import java.io.InputStreamReader


@OptIn(ExperimentalStdlibApi::class)
fun readEmojiFromAssets(contex: Context): LiveData<Map<String, List<EmojiBean>>> {
    val muLiveData = MutableLiveData<Map<String, List<EmojiBean>>>()
    val inputStringReader = InputStreamReader(contex.assets.open("emoji_filter.json"))
    val result = BufferedReader(inputStringReader).use {
        val text = it.readText()
        val jsonAdapter = Moshi.Builder().build().adapter<List<EmojiBean>>()
        try {
            jsonAdapter.fromJson(text)?.groupBy { bean ->
                bean.group
            }?.filter { map ->
                map.value.isNotEmpty()
            } ?: mapOf<String, List<EmojiBean>>()
        } catch (e: Exception) {
            e.printStackTrace()
            mapOf<String, List<EmojiBean>>()
        }
    }
    muLiveData.postValue(result)
    return muLiveData
}