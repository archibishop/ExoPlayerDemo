package com.example.demo.exoplayerdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.ProgressBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.exoplayer2.ExoPlayer




class MainActivity : Activity() {

    private lateinit var playerView: PlayerView
    private lateinit var componentListener: ComponentListener
    private var player: ExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.video_view)
        progressBar = findViewById(R.id.progress_bar)

        componentListener = ComponentListener()

    }

    private fun intializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        playerView.player =  player

        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)

        player?.addListener(componentListener)

        var uri =  Uri.parse("https://archive.org/download/ElephantsDream/ed_hd.mp4")

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
            it.addListener(componentListener)
            it.release()
            player = null
        }
    }

    inner class ComponentListener: Player.DefaultEventListener(), VideoRendererEventListener, AudioRendererEventListener {
        override fun onDroppedFrames(count: Int, elapsedMs: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoEnabled(counters: DecoderCounters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoSizeChanged(
            width: Int,
            height: Int,
            unappliedRotationDegrees: Int,
            pixelWidthHeightRatio: Float
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoDisabled(counters: DecoderCounters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoDecoderInitialized(
            decoderName: String?,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoInputFormatChanged(format: Format?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRenderedFirstFrame(surface: Surface?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioEnabled(counters: DecoderCounters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioInputFormatChanged(format: Format?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioSessionId(audioSessionId: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioDecoderInitialized(
            decoderName: String?,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAudioDisabled(counters: DecoderCounters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            var stateString: String?
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> stateString = "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    progressBar.visibility = View.VISIBLE
                }
                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -"
                    progressBar.visibility = View.GONE
                }
                ExoPlayer.STATE_ENDED -> stateString = "ExoPlayer.STATE_ENDED     -"
                else -> stateString = "UNKNOWN_STATE             -"
            }
        }
    }

}
