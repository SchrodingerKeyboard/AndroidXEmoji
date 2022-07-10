package com.androidx.emoji.emojiWidget

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidx.emoji.R
import com.androidx.emoji.beans.EmojiBean
import com.androidx.emoji.databinding.EmojiPanelAdapterLayoutBinding

class EmojiPanelAdapter(
    private val context: Context,
    private val onEmojiSelectedListener: OnEmojiSelectedListener,
    private val onEmojiDeleteListener: OnEmojiDeleteListener
) : RecyclerView.Adapter<EmojiPanelViewHolder>() {

    private var emojiMapData: Map<String, List<EmojiBean>>? = null

    fun updateEmojiData(data: Map<String, List<EmojiBean>>?) {
        this.emojiMapData = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiPanelViewHolder {

        val binding = EmojiPanelAdapterLayoutBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = binding.root.layoutParams ?: ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.layoutParams.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        return EmojiPanelViewHolder(binding,onEmojiSelectedListener)
    }

    override fun onBindViewHolder(holder: EmojiPanelViewHolder, position: Int) {
        holder.initData(emojiMapData?.map { it.value }?.get(holder.adapterPosition),onEmojiSelectedListener)
        holder.delImageView.setOnClickListener {
            onEmojiDeleteListener?.invoke()
        }
    }

    override fun getItemCount(): Int {
        return emojiMapData?.size ?: 0
    }
}

class EmojiPanelViewHolder(binding: EmojiPanelAdapterLayoutBinding,onEmojiSelectedListener: OnEmojiSelectedListener) : RecyclerView.ViewHolder(binding.root) {
    val recyclerView: RecyclerView
    val emojiListAdapter: EmojiListAdapter
    val delImageView: ImageView

    init {
        delImageView = binding.delImageView
        recyclerView = binding.emojiRecyclerView
        recyclerView.apply {
            layoutManager = GridLayoutManager(binding.root.context, 7, RecyclerView.VERTICAL, false)
            emojiListAdapter = EmojiListAdapter(binding.root.context,onEmojiSelectedListener)
            adapter = emojiListAdapter
        }
    }

    fun initData(emojiList: List<EmojiBean>?, onEmojiSelectedListener: OnEmojiSelectedListener) {
        emojiListAdapter.onEmojiSelectedListener = onEmojiSelectedListener
        emojiListAdapter.updateEmojiList(emojiList)
    }
}

class EmojiListAdapter(val context: Context, var onEmojiSelectedListener: OnEmojiSelectedListener) :
    RecyclerView.Adapter<EmojiListViewHolder>() {

    private var emojiList: List<EmojiBean>? = null
    fun updateEmojiList(emojiList: List<EmojiBean>?) {
        this.emojiList = emojiList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiListViewHolder {
        val viewHolder = EmojiListViewHolder(LayoutInflater.from(context).inflate(R.layout.emoji_list_item_layout, null),onEmojiSelectedListener)
        viewHolder.itemView.layoutParams = viewHolder.itemView.layoutParams ?: ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: EmojiListViewHolder, position: Int) {
        val emojiBean = emojiList?.get(holder.adapterPosition)
        holder.initData(emojiBean)
    }

    override fun getItemCount(): Int {
        return emojiList?.size ?: 0
    }
}

class EmojiListViewHolder(rootView: View,private val onEmojiSelectedListener: OnEmojiSelectedListener) : RecyclerView.ViewHolder(rootView) {
    private val textView: TextView
    private var emojiPopupWindow: EmojiPopupWindow? = null

    init {
        textView = rootView.findViewById(R.id.tvEmoji)
    }

    fun initData(emoji: EmojiBean?) {
        textView.text = emoji?.char
        itemView.setOnTouchListener { v, e ->
            if ((emojiPopupWindow?.isShowing == true) &&
                (e.action == MotionEvent.ACTION_UP
                        || e.action == MotionEvent.ACTION_POINTER_UP || e.action == MotionEvent.ACTION_CANCEL)
            ) {
                emojiPopupWindow?.dismiss()
                true
            }
            false
        }

        itemView.setOnLongClickListener {
            emojiPopupWindow = EmojiPopupWindow(itemView.context).apply {
                show(it, emoji)
            }
            return@setOnLongClickListener true
        }

        itemView.setOnClickListener {
            if(emoji == null) {
                return@setOnClickListener
            }
            Log.d("onEmojiSelectedListener","$onEmojiSelectedListener")
            onEmojiSelectedListener.invoke(emoji)
        }
    }
}



