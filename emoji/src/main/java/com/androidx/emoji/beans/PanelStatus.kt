package com.androidx.emoji.beans


data class PanelStatus(
    //功能面板里的Viewid集合
    val viewIds: List<Int> = listOf<Int>(),
    //是否执行动画中
    val isAniming: Boolean = false,
    //当前正在显示的view的Id
    val showViewId: Int? = null,
    //面板是否正在显示中
    val isShowing: Boolean = false,
    val isKeyboardShow: Boolean = false,
    //面板高度
    val height: Int = 0,
)

