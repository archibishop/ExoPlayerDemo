package com.example.demo.exoplayerdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class MainActivity : Activity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private val BAND_WIDTH_METER = DefaultBandwidthMeter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.video_view)

    }

    private fun intializePlayer() {

        var adaptiveTrackSelection = AdaptiveTrackSelection.Factory(BAND_WIDTH_METER)
        player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this)
            , DefaultTrackSelector(adaptiveTrackSelection)
            , DefaultLoadControl())
        playerView.player =  player

        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)

        var uri =  Uri.parse(getString(R.string.video_url_mp4))

        var mediaSource = buildMediaSource(uri)
        player?.prepare(mediaSource, true, true)
    }

    private fun buildMediaSource(uri:Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            intializePlayer()
        }
    }


    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            intializePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.release()
            player = null
        }
    }
}
