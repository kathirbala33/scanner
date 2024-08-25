package com.scanner

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.min


class GalleryScannerActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    private var cameraSource: CameraSource? = null
    private var detector: BarcodeDetector? = null
    private var intentData: String = ""
    private var imageUri: Uri? = null
    private val REQUEST_CAMERA_PERMISSION: Int = 200
    private val CAMERA_REQUEST: Int = 101
    private val TAG: String = "QR_CODE_SCANNER"
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var imageFile: File? = null
    private var uri: Uri? = null

    private var imageView: ImageView? = null
    private var textViewResultBody: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gallery_scanner)
        initComponents()
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
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                    // Use the bitmap (e.g., display in an ImageView)
                    try {
                        val imageUri: Uri? = result.data!!.data
                        val bitmap = decodeBitmapUri(this, imageUri!!)
                        if (detector!!.isOperational && bitmap != null) {
                            imageView!!.setImageBitmap(bitmap)
                            val frame = Frame.Builder().setBitmap(bitmap).build()
                            val barCodes = detector!!.detect(frame)
                            setBarCode(barCodes)
                        } else {
                            textViewResultBody!!.text = "Detector initialisation failed"
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Failed to load Image",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.e(TAG, e.toString())
                    }
                }
            }

        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                launchMediaScanIntent()
//                val data: Intent? = result.data
//                // Handle the camera photo here
//                val extras = data?.extras
//                val imageBitmap = extras?.get("data") as? Bitmap
//                // Do something with the bitmap, e.g., display it in an ImageView
//                findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)


                try {
                    val bitmap = decodeBitmapUri(this, imageUri!!)
                    if (detector!!.isOperational && bitmap != null) {
                        imageView!!.setImageBitmap(bitmap)
                        val frame = Frame.Builder().setBitmap(bitmap).build()
                        val barCodes = detector!!.detect(frame)
                        setBarCode(barCodes)
                    } else {
                        textViewResultBody!!.text = "Detector initialisation failed"
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    private fun getGalleryPicture() {
        try {
            val takePictureIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)

            if (takePictureIntent.resolveActivity(packageManager) != null) {
                galleryLauncher.launch(takePictureIntent)
            }
        } catch (e: Exception) {
            Log.d("##PathFromCamera", e.toString())
        }


    }

    private fun initComponents() {
        imageView = findViewById(R.id.imageView)
        textViewResultBody = findViewById(R.id.textViewResultsBody)
        findViewById<View>(R.id.buttonOpenCamera).setOnClickListener {
            askForPermissions()
        }

        detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()

        /*  if (!detector.isOperational()) {
              findViewById<TextView>(R.id.textViewResultsBody).text = "Detector initialisation failed"
              return
          }*/


    }

    private fun launchMediaScanIntent() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.setData(imageUri)
        this.sendBroadcast(mediaScanIntent)
    }


    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED)
        ) {
            // Permissions are already granted, proceed to open the camera
            getGalleryPicture()
        } else {
            // Request the appropriate permissions based on the Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_MEDIA_IMAGES// If you need to access videos as well
                    )
                )
            } else {
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }


    private val requestMultiplePermissionsLauncher1 =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result: Map<String, Boolean> ->
            val cameraPermissionGranted =
                result.getOrDefault(android.Manifest.permission.CAMERA, false)
            val writeStoragePermissionGranted =
                result.getOrDefault(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, false)
            if (cameraPermissionGranted && writeStoragePermissionGranted) {
//                openCamera()
                getGalleryPicture()
            } else {

            }
        }
    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result: Map<String, Boolean> ->
            val cameraPermissionGranted =
                result.getOrDefault(android.Manifest.permission.CAMERA, false)
            val readImagesPermissionGranted =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.getOrDefault(android.Manifest.permission.READ_MEDIA_IMAGES, false)
                } else {

                    result.getOrDefault(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, false)

                }


            // If the camera permission is granted and at least one of the media permissions is granted
            if (cameraPermissionGranted && (readImagesPermissionGranted)) {
                // Your logic to handle camera and storage permissions, e.g., open the camera
                getGalleryPicture()
            } else {
                // Handle the case where permissions are not granted
                // Maybe show a message to the user or disable the functionality
            }
        }


    private fun takeBarcodePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "barcode.jpg")
        imageUri = FileProvider.getUriForFile(
            this@GalleryScannerActivity,
            BuildConfig.APPLICATION_ID + ".provider", photo
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            cameraActivityResultLauncher.launch(cameraIntent)
        }
    }

    @Throws(FileNotFoundException::class)
    private fun decodeBitmapUri(ctx: Context, uri: Uri): Bitmap? {
        val targetW = 600
        val targetH = 600
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri), null, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        val scaleFactor =
            min((photoW / targetW).toDouble(), (photoH / targetH).toDouble()).toInt()
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeStream(
            ctx.contentResolver
                .openInputStream(uri), null, bmOptions
        )
    }


    private fun setBarCode(barCodes: SparseArray<Barcode>) {
        if (barCodes.size() == 0) {
            textViewResultBody!!.text = "No barcode could be detected. Please try again."
            return
        }
        for (index in 0 until barCodes.size()) {
            val code = barCodes.valueAt(index)
//            textViewResultBody.setText(textViewResultBody.getText() + "\n" + code.displayValue + "\n")
            textViewResultBody!!.text =
                textViewResultBody!!.text.toString() + "\n" + code.displayValue + "\n"
            copyToClipBoard(code.displayValue)
            val type = barCodes.valueAt(index).valueFormat
            when (type) {
                Barcode.CONTACT_INFO -> Log.i(TAG, code.contactInfo.title)
                Barcode.EMAIL -> Log.i(TAG, code.displayValue)
                Barcode.ISBN -> Log.i(TAG, code.rawValue)
                Barcode.PHONE -> Log.i(TAG, code.phone.number)
                Barcode.PRODUCT -> Log.i(TAG, code.rawValue)
                Barcode.SMS -> Log.i(TAG, code.sms.message)
                Barcode.TEXT -> Log.i(TAG, code.displayValue)
                Barcode.URL -> Log.i(TAG, "url: " + code.displayValue)
                Barcode.WIFI -> Log.i(TAG, code.wifi.ssid)
                Barcode.GEO -> Log.i(TAG, code.geoPoint.lat.toString() + ":" + code.geoPoint.lng)
                Barcode.CALENDAR_EVENT -> Log.i(TAG, code.calendarEvent.description)
                Barcode.DRIVER_LICENSE -> Log.i(TAG, code.driverLicense.licenseNumber)
                else -> Log.i(TAG, code.rawValue)
            }
        }
    }

    private fun copyToClipBoard(text: String) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR code Scanner", text)
        clipboard.setPrimaryClip(clip)
    }
}