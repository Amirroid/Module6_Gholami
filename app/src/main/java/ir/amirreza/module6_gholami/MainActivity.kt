package ir.amirreza.module6_gholami

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
import ir.amirreza.module6_gholami.ui.features.home.HomeScreen
import ir.amirreza.module6_gholami.ui.features.qr_code.QrCodeScreen
import ir.amirreza.module6_gholami.ui.features.qr_code_scanner.QrCodeScannerScreen
import ir.amirreza.module6_gholami.ui.features.register.RegisterScreen
import ir.amirreza.module6_gholami.ui.theme.Module6_GholamiTheme
import ir.amirreza.module6_gholami.utils.AppPages

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        composable(AppPages.QrCode.route + "?key={key}", arguments = listOf(navArgument("key") {
            type = NavType.StringType
        })) {
            val data = it.arguments?.getString("key") ?: ""
            QrCodeScreen(data)
        }
    }
}
