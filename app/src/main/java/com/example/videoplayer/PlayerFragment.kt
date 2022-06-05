package com.example.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.videoplayer.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultLoadControl.*
import com.google.android.exoplayer2.util.Util

class PlayerFragment : Fragment() {

    // 获取XML文件中的视图树引用
    private lateinit var binding: FragmentPlayerBinding

    // 播放流媒体的ExoPlayer对象
    private var player: ExoPlayer? = null

    // 快进、快退时间
    private val fastForwardIncrement = 15000L
    private val rewindIncrement = 15000L

    // 释放播放器资源以及销毁播放器相关变量
    private var playWhenReady = true    // 存储播放/暂停状态
    private var currentWindow = 0       // 存储当前播放位置
    private var playbackPosition = 0L   // 存储当前窗口索引

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_player, container, false
        )
        return binding.root
    }

    // Android API >= 24的版本在OnStart中初始化播放器
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            playVideo()
        }
    }

    // Android API < 24 的版本在OnResume中初始化播放器
    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            playVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    // 初始化播放配置参数
    private fun initializeConfiguration(): DefaultLoadControl {
        var bufferSize = PlayerFragmentArgs.fromBundle(requireArguments()).bufferSize * 60_000
        if (bufferSize < 50_000) {
            bufferSize = 50_000
            Toast.makeText(requireContext(), "缓冲区大小不能小于50秒", Toast.LENGTH_SHORT).show()
        }
        return Builder()
            .setBufferDurationsMs(50_000, bufferSize, 2500, 5000)
            .build()
    }

    // 初始化exoPlayer对象
    private fun initializePlayer(mediaAddress: String, loadControl: DefaultLoadControl) {
        player = ExoPlayer.Builder(requireContext())  //requireContext()用来获取fragment的宿主activity
            .setSeekForwardIncrementMs(fastForwardIncrement)
            .setSeekBackIncrementMs(rewindIncrement)
            .setLoadControl(loadControl)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(mediaAddress)
                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()  // 告知播放器获取播放所需的所有资源
            }
    }

    private fun playVideo() {
        val loadControl = initializeConfiguration()
        val mediaAddress = PlayerFragmentArgs.fromBundle(requireArguments()).mediaUrl
        initializePlayer(mediaAddress, loadControl)
    }

    // 释放播放器资源以及销毁播放器
    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }
}