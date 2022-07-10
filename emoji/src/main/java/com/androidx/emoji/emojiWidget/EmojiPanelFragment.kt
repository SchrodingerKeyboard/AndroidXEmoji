package com.androidx.emoji.emojiWidget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.androidx.emoji.beans.EmojiBean
import com.androidx.emoji.databinding.EmojiPanelLayoutBinding
import kotlinx.coroutines.launch

typealias OnEmojiSelectedListener = (EmojiBean) -> Unit
typealias OnEmojiDeleteListener = () -> Unit

class EmojiPanelFragment : Fragment() {

    private var emojiGroupAdapter: EmojiGroupAdapter? = null
    private var emojiPanelAdapter: EmojiPanelAdapter? = null
    private var binding: EmojiPanelLayoutBinding? = null
    private val viewModel: EmojiPanelViewModel by viewModels()

    var onEmojiSelectedListener: OnEmojiSelectedListener? = null
    var onEmojiDeleteListener: OnEmojiDeleteListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = EmojiPanelLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.emojiGroupRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            emojiGroupAdapter = EmojiGroupAdapter(context) {
                if (binding?.emojiGroupViewPager?.currentItem != it) {
                    binding?.emojiGroupViewPager?.setCurrentItem(it, false)
                }
                updateEmojiGroupSelectPosition(it)
            }
            adapter = emojiGroupAdapter
        }

        binding?.emojiGroupViewPager?.apply {
            emojiPanelAdapter = EmojiPanelAdapter(context,onEmojiSelectedListener?:{},onEmojiDeleteListener?:{})
            emojiPanelAdapter
            adapter = emojiPanelAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateEmojiGroupSelectPosition(position)
                    super.onPageSelected(position)
                }
            })
        }
        initialize()
    }

    private fun updateEmojiGroupSelectPosition(index: Int) {
        if (emojiGroupAdapter?.selectedPosition() != index) {
            emojiGroupAdapter?.updateAndNotifySelectPosition(index)
        }
        val linearLayoutManager = (binding?.emojiGroupRecyclerView?.layoutManager as? LinearLayoutManager) ?: return
        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()

        val scrollTo = if (index + 2 >= lastPosition) {
            if (index + 2 <= (emojiPanelAdapter?.itemCount ?: 0)) {
                index + 2
            } else if (index + 1 <= (emojiPanelAdapter?.itemCount ?: 0)) {
                index + 1
            } else {
                index
            }
        } else if (index - 2 <= firstPosition) {
            if (index - 2 >= 0) {
                index - 2
            } else if (index - 1 >= 0) {
                index - 1
            } else {
                index
            }
        } else {
            index
        }
        linearLayoutManager.scrollToPosition(if (scrollTo < 0) 0 else scrollTo)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initialize() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.emojiPanelState.collect {
                    if (it.emojiData.isEmpty()) {
                        return@collect
                    }
                    emojiGroupAdapter?.updateEmojiGroupList(it.emojiData.map { it.value.first().char })
                    emojiPanelAdapter?.updateEmojiData(it.emojiData)
                }
            }
        }

    }

}





