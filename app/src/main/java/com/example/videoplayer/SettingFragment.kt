package com.example.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.videoplayer.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var bufferSize = BUFFER_SIZE

    private var mediaUrl: String? = null

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_setting, container, false
        )

        binding.playButton.setOnClickListener {
            playMedia()
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("mediaUrl", mediaUrl)
        outState.putInt("bufferSize", bufferSize)
    }

    // 跳转到PlayerFragment播放视频
    private fun playMedia() {
        mediaUrl = binding.mediaUrl.text.toString()
        if (binding.bufferSize.text.toString() != "") {
            bufferSize = binding.bufferSize.text.toString().toInt()
        }
        if (mediaUrl != null) {
            NavHostFragment.findNavController(this).navigate(
                SettingFragmentDirections.actionSettingFragmentToPlayerFragment(
                    mediaUrl.toString(),
                    bufferSize
                )
            )
        }
    }

    companion object {
        const val BUFFER_SIZE = 5 // 默认缓冲区大小 5分钟
    }

}