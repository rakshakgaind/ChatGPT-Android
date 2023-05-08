package com.ultivic.chatgpt.fragments

import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.material.imageview.ShapeableImageView
import com.ultivic.chatgpt.R
import com.ultivic.chatgpt.adapters.ChatAdapter
import com.ultivic.chatgpt.base.BaseFragment
import com.ultivic.chatgpt.databinding.FragmentHomeBinding
import com.ultivic.chatgpt.databinding.ItemAnswerLayoutBinding
import com.ultivic.chatgpt.databinding.PopupWindowOptionsBinding
import com.ultivic.chatgpt.model.ImageModel
import com.ultivic.chatgpt.model.QuestionModel
import com.ultivic.chatgpt.utils.MyConstants
import com.ultivic.chatgpt.utils.MyConstants.ChatGptResponse
import com.ultivic.chatgpt.utils.MyConstants.UserInput
import com.ultivic.chatgpt.utils.PermissionManager.isPermission
import com.ultivic.chatgpt.utils.Resource
import com.ultivic.chatgpt.utils.createAnyDialog
import com.ultivic.chatgpt.utils.hideKeyboard
import com.ultivic.chatgpt.utils.isPackageInstalled
import com.ultivic.chatgpt.utils.rateApp
import com.ultivic.chatgpt.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.lang.NullPointerException
import java.util.Locale
import kotlin.math.roundToInt


private const val TAG = "HomeFragment"
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private lateinit var chatAdapter: ChatAdapter
    private val speechRecognizer: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(requireContext()) }
    private val recognizeIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    private val dialogGoogle by lazy {
        requireActivity().createAnyDialog("Warning", "Please install Google Application") {
            requireContext().rateApp(MyConstants.GOOGLE_LLC)
        }
    }
    private val viewModel: MainViewModel by activityViewModels()
    private var imgSound: ShapeableImageView? = null
    private var oldItemData: String? = null

    override fun initUI(view: View) {
        binding = FragmentHomeBinding.bind(view)
        chatAdapter = ChatAdapter()
        binding?.recyclerview?.adapter = chatAdapter

        if (!isPermission(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private var job: Job? = null

    override fun listeners() {

        job?.cancel()
        job = null
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                binding?.typingAnim?.visibility = View.GONE
                addData(getString(R.string.hi_there), ChatGptResponse)
            }
        }
        binding?.txtInput?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                delay(90)
                withContext(Dispatchers.Main) {
                    if (viewModel.getList().size != 0) binding?.recyclerview?.scrollToPosition(viewModel.getList().size - 1)
                }
            }
        }
        binding?.btnSend?.setOnClickListener {
            if (binding?.txtInput?.text?.trim()?.isNotEmpty() == true) {
                addData(binding?.txtInput?.text?.trim().toString(), UserInput)
                val model = QuestionModel(prompt = binding?.txtInput?.text.toString())
                viewModel.askQuestion(model)
                viewModel.isGenerateImage = false
                context?.hideKeyboard(it)
                speechRecognizer.stopListening()
                binding?.txtInput?.text = null
            }
        }

        chatAdapter.onPlay = { data, imageView ->
            if (imgSound != null) {
                imgSound?.setImageResource(R.drawable.ic_sound_mute)
            }
            imgSound = imageView

            if (oldItemData == data) {
                if (!viewModel.textToSpeech.isSpeaking) {
                    speechListener(data, imageView)
                } else {
                    viewModel.textToSpeech.stop()
                    imageView.setImageResource(R.drawable.ic_sound_mute)
                    imgSound?.setImageResource(R.drawable.ic_sound_mute)
                }
            } else {
                speechListener(data, imageView)
            }
            oldItemData = data
        }

        chatAdapter.onOtherOptions = { data, view ->
            showPopupWindow(view, data)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                Log.i("onReadyForSpeech", "onReadyForSpeech")
                binding?.txtInput?.hint = getString(R.string.listening)
                binding?.micAnim?.visibility = View.VISIBLE
                binding?.btnMic?.visibility = View.GONE
            }

            override fun onBeginningOfSpeech() {
                Log.i("onBeginningOfSpeech: ", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(p0: Float) {
                Log.i("onRmsChanged", "onRmsChanged $p0")
            }

            override fun onBufferReceived(p0: ByteArray?) {
                Log.i("onBufferReceived", "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.i("onEndOfSpeech", "onEndOfSpeech")
                if (binding?.typingAnim?.visibility != View.VISIBLE) binding?.txtInput?.hint = getString(R.string.ask_question)
                binding?.micAnim?.visibility = View.GONE
                binding?.btnMic?.visibility = View.VISIBLE
            }

            override fun onError(p0: Int) {
                Log.i("onError", "onError : $p0")
                if (binding?.typingAnim?.visibility != View.VISIBLE) binding?.txtInput?.hint = getString(R.string.ask_question)
                binding?.micAnim?.visibility = View.GONE
                binding?.btnMic?.visibility = View.VISIBLE
            }

            override fun onResults(result: Bundle?) {
                CoroutineScope(Dispatchers.Main).launch {
                    val input = " "+(result?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0))+" "
                    binding?.txtInput?.text?.insert(binding?.txtInput?.selectionStart ?: 0, input)
                    binding?.txtInput?.setSelection(binding?.txtInput?.text?.length ?: 0)
                    //for direct api hit
//                    if ((binding?.txtInput?.text ?: "") != "") binding?.btnSend?.performClick()
                }
            }

            override fun onPartialResults(p0: Bundle?) {
                Log.i("onPartialResults", "onPartialResults")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
                Log.i("onEvent", "onEvent")
            }
        })

        binding?.btnMic?.setOnClickListener {
            if (!it.context.packageManager.isPackageInstalled(MyConstants.GOOGLE_LLC)) {
                dialogGoogle.show()
                return@setOnClickListener
            }
            speechRecognizer.startListening(recognizeIntent)
        }
        binding?.micAnim?.setOnClickListener {
            if (binding?.typingAnim?.visibility != View.VISIBLE) binding?.txtInput?.hint = getString(R.string.ask_question)
            binding?.micAnim?.visibility = View.GONE
            binding?.btnMic?.visibility = View.VISIBLE
            speechRecognizer.cancel()
        }

    }

    private fun speechListener(data: String, imageView: ShapeableImageView) {
        viewModel.setVoice(data)
        viewModel.textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                imageView.setImageResource(R.drawable.ic_sound)
            }

            override fun onDone(p0: String?) {
                imageView.setImageResource(R.drawable.ic_sound_mute)
            }

            override fun onError(p0: String?) {
                Log.e("onError:", "Text to Speech error : $p0")
                imageView.setImageResource(R.drawable.ic_sound_mute)
                Toast.makeText(requireContext(), "Can not complete request at the moment", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.textToSpeech
    }

    private fun showPopupWindow(anchor: View, data: String) {
        val bindingOptions = PopupWindowOptionsBinding.inflate(layoutInflater)
        val bindingAnswer = ItemAnswerLayoutBinding.inflate(layoutInflater)
        val views = bindingOptions.root
        val popupWindow = PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            contentView = views
            width = resources.getDimension(com.intuit.sdp.R.dimen._130sdp).roundToInt()
            height = resources.getDimension(com.intuit.sdp.R.dimen._70sdp).roundToInt()
        }.also { popupWindow ->
            Log.i("showPopupWindow:", "y position ${(-0.2 * anchor.y).roundToInt()}")
            popupWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            val location = IntArray(2)
            bindingAnswer.icOptions.getLocationOnScreen(location)
            popupWindow.showAsDropDown(anchor,location[0]-40,location[1], Gravity.END)
        }
        bindingOptions.icOptionsArrow.tag = true
        bindingOptions.icOptionsArrow.setOnClickListener {
            if ((bindingOptions.icOptionsArrow.tag as? Boolean) == true) {
                bindingOptions.constraintOptionsGp.visibility = View.VISIBLE
                bindingOptions.viewTwo.visibility = View.VISIBLE
                bindingOptions.icOptionsArrow.setImageResource(R.drawable.ic_up_arrjow)
                bindingOptions.icOptionsArrow.tag = false
                popupWindow.update(anchor,resources.getDimension(com.intuit.sdp.R.dimen._130sdp).roundToInt(), resources.getDimension(com.intuit.sdp.R.dimen._220sdp).roundToInt())
            } else {
                bindingOptions.icOptionsArrow.tag = true
                bindingOptions.constraintOptionsGp.visibility = View.GONE
                bindingOptions.viewTwo.visibility = View.GONE
                bindingOptions.icOptionsArrow.setImageResource(R.drawable.ic_arrow_down)
                popupWindow.update(anchor, resources.getDimension(com.intuit.sdp.R.dimen._130sdp).roundToInt(), resources.getDimension(com.intuit.sdp.R.dimen._70sdp).roundToInt())
            }
        }
        bindingOptions.txtGenerateImage.setOnClickListener {
            val imageModel = ImageModel(prompt = data)
            viewModel.sendImage(imageModel)
            viewModel.isGenerateImage = true
            popupWindow.dismiss()
        }
        bindingOptions.inHindi.setOnClickListener {
            viewModel.isGenerateImage = false
            viewModel.translateTo = "Translate in Hindi"
            val model = QuestionModel(prompt = data + "\t${viewModel.translateTo}")
            viewModel.askQuestion(model)
            popupWindow.dismiss()
        }

        bindingOptions.inJapanese.setOnClickListener {
            viewModel.isGenerateImage = false
            viewModel.translateTo = "Translate in Japanese"
            val model = QuestionModel(prompt = data + "\t${viewModel.translateTo}")
            viewModel.askQuestion(model)
            popupWindow.dismiss()
        }
        bindingOptions.inSpanish.setOnClickListener {
            viewModel.isGenerateImage = false
            viewModel.translateTo = "Translate in Spanish"
            val model = QuestionModel(prompt = data + "\t${viewModel.translateTo}")
            viewModel.askQuestion(model)
            popupWindow.dismiss()
        }

    }

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun observers() {
        viewModel.getAnswerResponse().observe(this.viewLifecycleOwner) {
            Log.i("observers: ", "$it")
            when (it) {
                is Resource.Error -> {
                    addData("${it.message}", ChatGptResponse)
                }

                is Resource.Loading -> {
                    progress(it.isLoading ?: false)
                }

                is Resource.Success -> {
                    var d = it.data?.choices?.get(0)?.text?.replace("\n", "")?.replace("\t", "")
                    if (d == "") d = getString(R.string.I_am_sorry)
                    addData(d ?: "", ChatGptResponse)
                }

                null -> {
                    Log.i("observers: ", "response not get")
                }
            }
        }
        viewModel.getImageResponse().observe(this.viewLifecycleOwner) {
            Log.i("getImageResponse", "$it")
            when (it) {
                is Resource.Error -> addData("${it.message}", ChatGptResponse)
                is Resource.Loading -> progress(it.isLoading ?: false)
                is Resource.Success -> {
                    addData(it.data?.data?.get(0)?.url ?: "", ChatGptResponse)
                }

                null -> {
                    Log.i("observers: ", "response not get")
                }
            }
        }
    }

    private var permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.i("Permissions", "isPermissionGranted: $isGranted")
        } else {
            Toast.makeText(requireContext(), getString(R.string.please_grant_permission_from_setting), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addData(text: String, inputType: Int) {
        viewModel.addData(data = text, tag = inputType, isFirst = true, isGenerateImage = viewModel.isGenerateImage)
        chatAdapter.loadData(viewModel.getList())
        if (viewModel.getList().isNotEmpty()) binding?.recyclerview?.scrollToPosition(viewModel.getList().size - 1)
    }

    override fun onDestroy() {
        binding = null
        speechRecognizer.destroy()
        super.onDestroy()
    }

    private fun progress(isLoading: Boolean?) {
        if (isLoading == true) {
            binding?.typingAnim?.visibility = View.VISIBLE
            binding?.btnSend?.visibility = View.GONE
            binding?.btnMic?.visibility = View.GONE
            binding?.txtInput?.hint = getString(R.string.please_wait)
        } else {
            binding?.typingAnim?.visibility = View.GONE
            binding?.btnSend?.visibility = View.VISIBLE
            binding?.btnMic?.visibility = View.VISIBLE
            binding?.txtInput?.hint = getString(R.string.ask_question)
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        job = null
    }
}