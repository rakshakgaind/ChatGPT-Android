package com.ultivic.chatgpt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ultivic.chatgpt.MainActivity
import com.ultivic.chatgpt.R
import com.ultivic.chatgpt.adapters.OnBoardingAdapter
import com.ultivic.chatgpt.databinding.ActivityOnboardingBinding
import com.ultivic.chatgpt.model.OnboardingData


class OnboardingActivity : BaseAcitvity() {
    private val binding by lazy { ActivityOnboardingBinding.inflate(layoutInflater) }
    private val snapHelper = PagerSnapHelper()
    private var count = 0
    private val sharedPreferences by lazy { this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE) }
    private val shareEditor by lazy { sharedPreferences.edit() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val list = ArrayList<OnboardingData>()

        list.apply {
            add(
                OnboardingData(
                    R.drawable.ic_sun,
                    getString(R.string.examples),
                    getString(R.string.explain_quantum_computing_in_simple_terms),
                    getString(R.string.got_any_creative_ideas_for_a_year_old_birthday),
                    getString(R.string.how_do_i_make_http_request_in_javascript)
                )
            )

            add(
                OnboardingData(
                    R.drawable.ic_electric,
                    getString(R.string.capabilities),
                    getString(R.string.remember_what_user_said_earlier),
                    getString(R.string.allow_user_to_provide_corrections),
                    getString(R.string.trained_to_decline_inappropriate_request)
                )
            )
            add(
                OnboardingData(
                    R.drawable.ic_warning,
                    getString(R.string.limitations),
                    getString(R.string.remember_what_user_said_earlier),
                    getString(R.string.allow_user_to_provide_corrections),
                    getString(R.string.trained_to_decline_inappropriate_request)
                )
            )
        }

        val onBoardingAdapter = OnBoardingAdapter()
        binding.recyclerview.adapter = onBoardingAdapter
        onBoardingAdapter.addData(list)
        snapHelper.attachToRecyclerView(binding.recyclerview)
        toggleBars(binding.barViewOne, binding.barViewTwo, binding.barViewThree)

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    bottomBars(firstVisiblePosition)
                    Log.i("onScrollStateChanged:", "$firstVisiblePosition")
                }
            }
        })
        binding.btnNext.setOnClickListener {
            when(count){
                0 -> {
                    binding.recyclerview.scrollToPosition(count +1)
                    bottomBars(count+1)
                }
                1 -> {
                    binding.recyclerview.scrollToPosition(count +1)
                    bottomBars(count+1)
                }
                2 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    shareEditor.putBoolean("isFirst",true)
                    shareEditor.commit()
                }
            }
        }
    }

    private fun bottomBars(firstVisiblePosition: Int) {

        when (firstVisiblePosition) {
            0 -> {
                toggleBars(binding.barViewOne, binding.barViewTwo, binding.barViewThree)
                count = 0
            }
            1 -> {
                toggleBars(binding.barViewTwo, binding.barViewOne, binding.barViewThree)
                count = 1
            }
            2 -> {
                toggleBars(binding.barViewThree, binding.barViewTwo, binding.barViewOne)
                count = 2
            }
        }

    }

    private fun toggleBars(view_one: View, view_two: View, view_three: View) {
        view_one.background = ContextCompat.getDrawable(this, R.drawable.bg_gradient_blue_round)
        view_two.background = ContextCompat.getDrawable(this, R.drawable.bg_gray_rounded)
        view_three.background = ContextCompat.getDrawable(this, R.drawable.bg_gray_rounded)
    }
}