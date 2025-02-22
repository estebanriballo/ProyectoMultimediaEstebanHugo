package com.example.proyectomultimediaestebanhugo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectomultimediaestebanhugo.databinding.ActivityGrabarVideoBinding
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GrabarVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGrabarVideoBinding
    private lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraId: String
    private lateinit var cameraExecutor: ExecutorService
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var videoFile: File
    private lateinit var photoFile: File

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
                R.id.nav_reproducir_musica -> {
                    val intent = Intent(this, ReproducirMusicaActivity::class.java)
                    startActivity(intent)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        textureView = binding.viewFinder
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        binding.buttonPhoto.setOnClickListener {
            takePhoto()
        }

        binding.buttonVideo.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                startPreview()
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        }, null)
    }

    private fun startPreview() {
        val texture = textureView.surfaceTexture
        texture?.setDefaultBufferSize(1920, 1080)
        val surface = Surface(texture)

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)

        cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                cameraCaptureSession = session
                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e("GrabarVideoActivity", "Configuration failed")
            }
        }, null)
    }

    private fun takePhoto() {
        textureView.bitmap?.let { bitmap ->
            photoFile = File(externalCacheDir, "photo.jpg")
            FileOutputStream(photoFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            binding.imageViewPhoto.setImageBitmap(bitmap)
            Log.d("GrabarVideoActivity", "Foto guardada en: ${photoFile.absolutePath}")
        }
    }

    private fun startRecording() {
        videoFile = File(externalCacheDir, "video.mp4")
        mediaRecorder = MediaRecorder().apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(videoFile.absolutePath)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoSize(1920, 1080)
            setVideoFrameRate(30)
            prepare()
            start()
        }
        isRecording = true
        binding.buttonVideo.text = "Detener"
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        isRecording = false
        binding.buttonVideo.text = "Grabar"
        Log.d("GrabarVideoActivity", "Video guardado en: ${videoFile.absolutePath}")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
        cameraExecutor.shutdown()
    }
}
