package com.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException


class LiveScannerActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    private var cameraSource: CameraSource? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var intentData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanner)
        initView()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button (back button)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Handle the back button press
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        permissionCamera()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

    private fun initialiseDetectorsAndSources() {
        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource =
            CameraSource.Builder(this, barcodeDetector!!).setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build()

        findViewById<SurfaceView>(R.id.surfaceView).holder.addCallback(object :
            SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                openCamera()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })

        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode?> {
            override fun release() {
                Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detections<Barcode?>) {
                val barCode = detections.detectedItems
                if (barCode.size() > 0) {
                    setBarCode(barCode)
                }
            }
        })
    }

    private fun setBarCode(barCode: SparseArray<Barcode?>) {
        findViewById<TextView>(R.id.txtBarcodeValue).post {
            intentData = barCode.valueAt(0)?.displayValue.toString()
            findViewById<TextView>(R.id.txtBarcodeValue).text = intentData
            copyToClipBoard(intentData)
        }
    }

    private fun copyToClipBoard(text: String) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR code Scanner", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun initView() {
        permissionCamera()
    }

    private fun permissionCamera() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
    }


    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this@LiveScannerActivity, android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraSource?.start(findViewById<SurfaceView>(R.id.surfaceView).holder)
            } else {
                ActivityCompat.requestPermissions(
                    this@LiveScannerActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}