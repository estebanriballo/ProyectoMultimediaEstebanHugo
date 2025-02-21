package com.example.proyectomultimediaestebanhugo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectomultimediaestebanhugo.databinding.ActivityReproducirMusicaBinding
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager

class ReproducirMusicaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReproducirMusicaBinding
    private lateinit var exoPlayer: ExoPlayer

    private val albumes = listOf(
        Album("Views", listOf(
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(11) Controlla.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(12) One Dance (feat. Wizkid & Kyla).mp3"),
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(16) Too Good (feat. Rihanna).mp3")
        )),
        Album("Scorpion", listOf(
            Uri.parse("file:///storage/emulated/0/Download/scorpion_201807/5 God's Plan.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/scorpion_201807/9 In My Feelings.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/scorpion_201807/5 Finesse.mp3")
        )),
        Album("IYRTITL", listOf(
            Uri.parse("file:///storage/emulated/0/Download/drake-IYRTITL/Drake - Energy.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/drake-IYRTITL/Drake - Madonna.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/drake-IYRTITL/Drake - No Tellin'.mp3")
        ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReproducirMusicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigationDrawer()

        if (checkPermission()) {
            setupPlayer()
        } else {
            requestPermission()
        }

        binding.recyclerViewAlbums.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAlbums.adapter = AlbumAdapter(albumes) { album ->
            playAlbum(album)
        }

    }

    private fun playAlbum(album: Album) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        album.songs.forEach { uri ->
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.addMediaItem(mediaItem)
        }

        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_grabar_audio -> startActivity(Intent(this, GrabarAudioActivity::class.java))
                R.id.nav_grabar_video -> startActivity(Intent(this, GrabarVideoActivity::class.java))
            }

            finish()

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun checkPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                setupPlayer()
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede reproducir música.", Toast.LENGTH_LONG).show()
            }
        }

        permissionLauncher.launch(permission)
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer

        val canciones = listOf(
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(11) Controlla.mp3"),
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(12) One Dance (feat. Wizkid & Kyla).mp3"),
            Uri.parse("file:///storage/emulated/0/Download/views-full-tracks/(16) Too Good (feat. Rihanna).mp3")
        )

        exoPlayer.stop()

        exoPlayer.clearMediaItems()

        canciones.forEach { uri ->
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.addMediaItem(mediaItem)
        }

        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
