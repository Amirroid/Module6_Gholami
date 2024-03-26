package ir.amirreza.module6_gholami.data.states

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

data class AppState(
    val navigation: NavHostController,
    val scope: CoroutineScope
)

@Composable
fun rememberAppState(): AppState {
    val navigation = rememberNavController()
    val scope = rememberCoroutineScope()
    return remember {
        AppState(navigation, scope)
    }
}

val LocaleAppState = staticCompositionLocalOf<AppState> { error("") }