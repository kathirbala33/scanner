package com.scanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        initView()
    }

    private fun initView() {
        findViewById<LinearLayout>(R.id.camera).setOnClickListener {
            val intent = Intent(this@HomeActivity, CameraScannerActivity::class.java)
            startActivity(intent)

        }
        findViewById<LinearLayout>(R.id.gallery).setOnClickListener {
            val intent = Intent(this@HomeActivity, GalleryScannerActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.live).setOnClickListener {
            val intent = Intent(this@HomeActivity, LiveScannerActivity::class.java)
            startActivity(intent)
        }
    }
}