package ir.amirreza.module6_gholami.ui.features

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.AppPages
import ir.amirreza.module6_gholami.utils.NothingSurfaceHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val appState = LocaleAppState.current
    val lifecycle = LocalLifecycleOwner.current
    val biometricManager = remember {
        BiometricManager.from(context)
    }

    val previewView = remember {
        PreviewView(context)
    }

    fun captureImage() {
        val processCameraProvider = ProcessCameraProvider.getInstance(
            context,
        )
        processCameraProvider.addListener(
            {
                val cameraFuture = processCameraProvider.get()
                val selector = CameraSelector.DEFAULT_FRONT_CAMERA
                val preview= Preview.Builder()
                    .build().apply { setSurfaceProvider(previewView.surfaceProvider) }
                val imageCapture = ImageCapture.Builder()
                    .build()
                try {
                    cameraFuture.unbindAll()
                    cameraFuture.bindToLifecycle(
                        lifecycle,
                        selector,
                        preview,
                        imageCapture
                    )
                    val contentValues = ContentValues().apply {
                        put(
                            MediaStore.Images.ImageColumns.DISPLAY_NAME,
                            "${System.currentTimeMillis()}.jpeg"
                        )
                        put(
                            MediaStore.Images.ImageColumns.MIME_TYPE,
                            "image/jpeg"
                        )
                        put(
                            MediaStore.Images.ImageColumns.RELATIVE_PATH,
                            "Pictures/Gholami_6"
                        )
                    }
                    imageCapture.takePicture(
                        ImageCapture.OutputFileOptions.Builder(
                            context.contentResolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        ).build(), ContextCompat.getMainExecutor(context),
                        object : OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
                                appState.scope.launch {
                                    delay(3000)
                                    Toast.makeText(context, "You're not login", Toast.LENGTH_SHORT).show()
                                    (context as FragmentActivity).finish()
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Toast.makeText(
                                    context,
                                    "Failed to save image",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }
                    )
                } catch (e: Exception) {
                    Log.e("DSfdsfds", "captureImage: ${e.message}")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }
    LaunchedEffect(key1 = Unit) {
        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
//                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("login")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build()
                val biometricPrompt = BiometricPrompt(
                    context as FragmentActivity,
                    ContextCompat.getMainExecutor(context),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            appState.navigation.navigate(AppPages.Home.route)
                            super.onAuthenticationSucceeded(result)
                        }

                        override fun onAuthenticationFailed() {
                            captureImage()
                            super.onAuthenticationFailed()
                        }

                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            captureImage()
                            super.onAuthenticationError(errorCode, errString)
                        }
                    }
                )
                biometricPrompt.authenticate(biometricPromptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(context, "Enrolled", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                    intent.putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    context.startActivity(intent)
                }
            }

            else -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }
    AndroidView(factory = { previewView })
}