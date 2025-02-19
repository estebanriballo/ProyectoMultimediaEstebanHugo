package com.example.proyectomultimediaestebanhugo
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectomultimediaestebanhugo.databinding.ActivityGrabarAudioBinding
import java.io.IOException

class GrabarAudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGrabarAudioBinding

    private var grabadora: MediaRecorder? = null
    private var reproductor: MediaPlayer? = null
    private var isRecording = false

    private lateinit var audioFilePath: String
    val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrabarAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(!checkPermissions()){
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ), PERMISSION_REQUEST_CODE
            )
        }

        audioFilePath = "${externalCacheDir?.absolutePath}/audio_recorded.3gp"

        binding.btnRecord.setOnClickListener {
            if(!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }

        binding.btnPlay.setOnClickListener {
            playRecording()
        }
    }

    private fun playRecording() {
        reproductor = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
                Toast.makeText(this@GrabarAudioActivity, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@GrabarAudioActivity, "Error al reproducir el audio", Toast.LENGTH_SHORT).show()
            }
        }

        reproductor?.setOnCompletionListener {
            reproductor?.release()
            reproductor = null
            Toast.makeText(this, "Reproducción finalizada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        grabadora?.apply {
            stop()
            release()
        }
        grabadora = null
        isRecording = false
        binding.btnRecord.text = "Comenzar Grabación"
        binding.btnPlay.isEnabled = true
        Toast.makeText(this, "Grabación finalizada", Toast.LENGTH_SHORT).show()
    }

    private fun startRecording() {
        try {
            grabadora = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                setMaxDuration(10000)
                setMaxFileSize(1024*1024)
                setOnInfoListener { _, what, _ ->
                    when (what) {
                        MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> {
                            Toast.makeText(this@GrabarAudioActivity, "Duración máxima alcanzada", Toast.LENGTH_SHORT).show()
                            stopRecording()
                        }
                        MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED -> {
                            Toast.makeText(this@GrabarAudioActivity, "Tamaño máximo alcanzado", Toast.LENGTH_SHORT).show()
                            stopRecording()
                        }
                    }
                }

                prepare()
                start()
            }
            isRecording = true
            binding.btnRecord.text = "Detener Grabación"
            binding.btnPlay.isEnabled = false
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions(): Boolean {
        val recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        return recordPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

