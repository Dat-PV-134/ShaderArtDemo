package com.rekoj134.shaderartdemo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rekoj134.shaderartdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Hide bottom navigation
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        initOpenGLES()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOpenGLES() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs32 = configurationInfo.reqGlEsVersion >= 0x30002

        if (supportsEs32) {
            val myRenderer = MyRenderer(this@MainActivity)
            binding.myGLSurfaceView.setEGLContextClientVersion(3)
            binding.myGLSurfaceView.setRenderer(myRenderer)
            rendererSet = true

            binding.myGLSurfaceView.setOnTouchListener { v, event ->
                event?.let {
                    val normalizeX = (event.x / v.width) * 2 - 1
                    val normalizeY = -((event.y / v.height) * 2 - 1)
                    if (it.action == MotionEvent.ACTION_DOWN) {
                        binding.myGLSurfaceView.queueEvent { myRenderer.handleTouchPress(normalizeX, normalizeY) }
                    } else if (it.action == MotionEvent.ACTION_MOVE) {
                        binding.myGLSurfaceView.queueEvent { myRenderer.handleTouchDrag(normalizeX, normalizeY) }
                    }
                    true
                }
                true
            }
        } else {
            Toast.makeText(this@MainActivity, "This device doesn't support OpenGL ES 3.2", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            binding.myGLSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            binding.myGLSurfaceView.onPause()
        }
    }
}