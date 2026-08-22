package com.anvorgueso.dexium.core.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Plays Pokémon cries. Not scoped, so each owning ViewModel gets its own instance and must
 * call [release] from `onCleared` — the same lifetime the guess game already used when this
 * was inline, just shared now that the detail screen needs it too.
 *
 * ExoPlayer requires every call on the thread that created it. Creation is deferred to the
 * first [play], which is always a UI callback, so the player is created and driven on main.
 */
class CryPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var exoPlayer: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                _isPlaying.value = false
            }
        }
    }

    fun play(url: String) {
        if (url.isBlank()) return
        val player = exoPlayer ?: ExoPlayer.Builder(context).build()
            .also { it.addListener(listener) }
            .also { exoPlayer = it }

        player.stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    fun release() {
        exoPlayer?.removeListener(listener)
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
    }

    companion object {
        fun cryUrlFor(pokemonId: Int): String =
            "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/$pokemonId.ogg"
    }
}
