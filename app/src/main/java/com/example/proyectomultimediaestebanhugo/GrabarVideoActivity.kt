package com.example.proyectomultimediaestebanhugo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectomultimediaestebanhugo.databinding.ActivityGrabarVideoBinding

class GrabarVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGrabarVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGrabarVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_grabar_audio -> {
                    val intent = Intent(this, GrabarAudioActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_grabar_audio -> {
                    val intent = Intent(this, GrabarAudioActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_reproducir_musica -> {
                    val intent = Intent(this, ReproducirMusicaActivity::class.java)
                    startActivity(intent)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}