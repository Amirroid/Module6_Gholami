package ir.amirreza.module6_gholami

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.ui.features.devices.DevicesScreen
import ir.amirreza.module6_gholami.ui.features.home.HomeScreen
import ir.amirreza.module6_gholami.ui.features.qr_code.QrCodeScreen
import ir.amirreza.module6_gholami.ui.features.qr_code_scanner.QrCodeScannerScreen
import ir.amirreza.module6_gholami.ui.features.receive_file.ReceiveFileScreen
import ir.amirreza.module6_gholami.ui.features.register.RegisterScreen
import ir.amirreza.module6_gholami.ui.features.send.SendFileScreen
import ir.amirreza.module6_gholami.ui.theme.Module6_GholamiTheme
import ir.amirreza.module6_gholami.utils.AppPages

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ),
                100
            )
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ),
                0
            )
        }
        setContent {
            Module6_GholamiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetUpNavHost()
                }
            }
        }
    }
}

@ExperimentalGetImage
@Composable
fun SetUpNavHost() {
    val appState = LocaleAppState.current
    val navigation = appState.navigation
    NavHost(navController = navigation, startDestination = AppPages.Home.route) {
        composable(AppPages.Register.route) {
            RegisterScreen()
        }
        composable(AppPages.Home.route) {
            HomeScreen()
        }
        composable(AppPages.QrCodeScanner.route) {
            QrCodeScannerScreen()
        }
        composable(
            AppPages.QrCode.route + "?key={key}&filename={filename}",
            arguments = listOf(navArgument("key") {
                type = NavType.StringType
            }, navArgument("filename") {
                type = NavType.StringType
            })
        ) {
            val data = it.arguments?.getString("key") ?: ""
            val filename = it.arguments?.getString("filename") ?: ""
            QrCodeScreen(data, filename)
        }
        composable(
            AppPages.Devices.route + "?filename={filename}",
            arguments = listOf(navArgument("filename") {
                type = NavType.StringType
            })
        ) {
            val filename = it.arguments?.getString("filename") ?: ""
            DevicesScreen(filename)
        }
        composable(
            AppPages.SendFile.route + "?filename={filename}&address={address}",
            arguments = listOf(navArgument("filename") {
                type = NavType.StringType
            }, navArgument("address") {
                type = NavType.StringType
            })
        ) {
            val filename = it.arguments?.getString("filename") ?: ""
            val address = it.arguments?.getString("address") ?: ""
            SendFileScreen(address)
        }
        composable(
            AppPages.ReceiveFile.route + "?key={key}",
            arguments = listOf(navArgument("key") {
                type = NavType.StringType
            })
        ) {
            val key = it.arguments?.getString("key") ?: ""
            ReceiveFileScreen()
        }
    }
}
