package com.ultivic.chatgpt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ultivic.chatgpt.databinding.ItemOnboardingBinding
import com.ultivic.chatgpt.model.OnboardingData

class OnBoardingAdapter :RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>(){

    private var list = ArrayList<OnboardingData>()
    lateinit var context :Context
    fun addData(list: ArrayList<OnboardingData>){
        this.list = list
    }

    inner class ViewHolder(val binding: ItemOnboardingBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context) ,parent,false))
    }

    override fun onBindViewHolder(holder: OnBoardingAdapter.ViewHolder, position: Int) {
        holder.binding.icon.setImageResource(list[position].icon)
        holder.binding.txtHeading.text = list[position].heading
        holder.binding.txtItemOne.text = list[position].itemOne
        holder.binding.txtItemTwo.text = list[position].itemTwo
        holder.binding.txtItemThree.text = list[position].itemThree
    }
    var onScroll: ((Int) -> Unit)? = null
    override fun getItemCount() = list.size
}