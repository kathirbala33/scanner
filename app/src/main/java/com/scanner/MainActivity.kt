package com.scanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scanner.ui.theme.ScannerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    var activity: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this

        enableEdgeToEdge()
        setContent {
            /* ScannerTheme {
                 Surface(
                 modifier = Modifier.fillMaxSize(),
                 color = MaterialTheme.colorScheme.background
             ) {
                 CenteredButton()
             }
             }*/
            MyCameraApp()
        }
    }
}


/*@Composable
fun CenteredButton() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                permissionCamera() }) {
                Text(text = "QR/Bar Code From Camera")
            }
            Button(onClick = { *//*TODO*//* }) {
                Text(text = "QR/Bar Code From Gallery")
            }
        }
    }

}*/

/*@Composable
fun permissionCamera() {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    } else {
        openCamera(activity)
    }
}*/

/*private fun openCamera(context: Context) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    resultLauncher.launch(intent)
}*/

/*private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val data: Intent = result.data
        val imageBitmap = data.extras?.get("data") as Bitmap
     }
}*/

@Composable
fun MyCameraApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera(context)
            } else {
                Toast.makeText(
                    context,
                    "Camera permission is required to use the camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                // Handle the captured image (e.g., display it in an ImageView)
            }
        }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = {
            coroutineScope.launch {
//                permissionCamera(context, requestPermissionLauncher)
            }
        }) {
            Text(text = "QR/Bar Code From Camera")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "QR/Bar Code From Gallery")
        }
    }


}

private fun openCamera(context: Context) {
    val activity = context as? Activity
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    activity?.startActivityForResult(intent, 2)
}

/*private fun permissionCamera(context: Context, requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    } else {
        openCamera(context)
    }
}*/

/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = { *//*TODO*//* }) {
            Text(
                text = "Send Email",
                style = TextStyle(fontSize = 15.sp)
            )
        }
        Button(onClick = { *//*TODO*//* }) {
            Text(
                text = "Send Email",
                style = TextStyle(fontSize = 15.sp)
            )
        }
        Button(onClick = { *//*TODO*//* }) {
            Text(
                text = "Call",
                style = TextStyle(fontSize = 15.sp)
            )
        }
    }
}*/


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScannerTheme {
//        Greeting("Android")
    }
}