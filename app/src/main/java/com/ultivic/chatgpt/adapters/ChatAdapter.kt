package com.ultivic.chatgpt.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.ultivic.chatgpt.R
import com.ultivic.chatgpt.databinding.ItemAnswerLayoutBinding
import com.ultivic.chatgpt.databinding.ItemQuestionLayoutBinding
import com.ultivic.chatgpt.model.QuesAnsData
import com.ultivic.chatgpt.utils.MyConstants.ChatGptResponse
import com.ultivic.chatgpt.utils.MyConstants.UserInput
import com.ultivic.chatgpt.utils.copyToClipBoard
import com.ultivic.chatgpt.utils.shareMediaFile

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list = ArrayList<QuesAnsData>()

    fun loadData(list: ArrayList<QuesAnsData>) {
        this.list.clear()
        this.list.addAll(list)
    }

    inner class QuestionViewHolder(private val binding: ItemQuestionLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: String) {
            binding.txtQuestion.text = list
        }
    }

    inner class AnswerViewHolder(private val binding: ItemAnswerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: QuesAnsData, position: Int) {
            if (position == list.size-1) {
                if (data.isGenerateImage) {
                    showImage(binding, data)
                } else {
                    binding.txtAnswer.visibility = View.VISIBLE
                    binding.icSound.visibility = View.VISIBLE
                    binding.icCopy.visibility = View.VISIBLE
                    binding.icOptions.visibility = View.VISIBLE


                    binding.imgResponse.visibility = View.GONE
                    if (list[adapterPosition].isFirst) {
//                        data.isFirst = false
//                        binding.txtAnswer.typeWriterEffect(data.data, 10L) { data.isFirst = false }
                        binding.txtAnswer.text = data.data
                    } else binding.txtAnswer.text = data.data
                }
            } else {
                if (data.isGenerateImage) {
                    showImage(binding, data)
                } else {
                    binding.txtAnswer.visibility = View.VISIBLE
                    binding.icSound.visibility = View.VISIBLE
                    binding.icCopy.visibility = View.VISIBLE
                    binding.icOptions.visibility = View.VISIBLE
                    binding.imgResponse.visibility = View.GONE
                    binding.txtAnswer.text = data.data
                }
            }
        }

        init {
            binding.icCopy.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val data = list[position]
                it.context.copyToClipBoard(data.data)
//                if (data.isGenerateImage){
//                    it.context.shareMediaFile(data.data.toUri())
//                }else{
//
//                }
            }
            binding.icSound.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val data = list[position]
                onPlay?.invoke(data.data,binding.icSound)
            }

            binding.icOptions.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val data = list[position]
                onOtherOptions?.invoke(data.data, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == UserInput) {
            QuestionViewHolder(ItemQuestionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            AnswerViewHolder(ItemAnswerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == UserInput) {
            (holder as QuestionViewHolder).bind(list[position].data)
        } else {
            (holder as AnswerViewHolder).bind(list[position], position)
        }
    }

//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return if (list[position].tag == UserInput) UserInput else ChatGptResponse
    }

    var onPlay: ((String,ShapeableImageView) -> Unit)? = null
    var onOtherOptions: ((String, View) -> Unit)? = null

    fun showImage(binding: ItemAnswerLayoutBinding, data: QuesAnsData) {
        binding.imgResponse.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.VISIBLE
        binding.txtAnswer.visibility = View.GONE
        binding.icSound.visibility = View.GONE
        binding.icCopy.visibility = View.GONE
        binding.icOptions.visibility = View.GONE
        Glide.with(binding.imgResponse).load(data.data).error(R.drawable.ic_error_found).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.i("onLoadFailed: ", "$e")
                    binding.progressCircular.visibility = View.GONE

                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    binding.progressCircular.visibility = View.GONE
                    return false
                }
            }).into(binding.imgResponse)
    }

}