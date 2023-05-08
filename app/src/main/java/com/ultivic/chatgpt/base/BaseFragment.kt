package com.ultivic.chatgpt.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ultivic.chatgpt.databinding.ItemAnswerLayoutBinding

abstract class BaseFragment(@LayoutRes id:Int) : Fragment(id) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
        listeners()
        observers()
    }

    abstract fun observers()

    abstract fun initUI(view: View)

    abstract fun listeners()

}