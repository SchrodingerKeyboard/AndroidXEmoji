package com.androidx.emoji.emojiWidget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.emoji.R

class EmojiGroupAdapter(private val context: Context,private val onSelectedPositionChange:((Int)->Unit)?) : RecyclerView.Adapter<EmojiGroupViewHolder>() {

    private var emojiGroupList: List<String>? = null
    private var _selectedPosition = 0
    fun selectedPosition() = _selectedPosition

    fun updateEmojiGroupList(emojiGroupList: List<String>?) {
        this.emojiGroupList = emojiGroupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiGroupViewHolder {
        return EmojiGroupViewHolder(LayoutInflater.from(context).inflate(R.layout.emoji_group_item_layout, null))
    }

    override fun onBindViewHolder(holder: EmojiGroupViewHolder, position: Int) {
        holder.initData(emojiGroupList?.get(holder.adapterPosition))
        holder.itemView.isSelected = holder.adapterPosition == _selectedPosition
        holder.itemView.setOnClickListener {
            updateAndNotifySelectPosition(holder.adapterPosition)
            onSelectedPositionChange?.invoke(position)
        }
    }

    fun updateAndNotifySelectPosition(newSelectedPosition: Int) {
        val oldPosition = _selectedPosition
        _selectedPosition = newSelectedPosition
        notifyItemChanged(oldPosition)
        notifyItemChanged(_selectedPosition)
    }

    override fun getItemCount(): Int {
        return emojiGroupList?.size ?: 0
    }
}

class EmojiGroupViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    private var tvEmoji: TextView

    init {
        tvEmoji = rootView.findViewById(R.id.tvEmoji)
    }

    fun initData(emoji: String?) {
        tvEmoji.text = emoji
    }
}