package ir.amirreza.module6_gholami.utils

sealed class AppPages(val route: String) {
    data object Register: AppPages("register")
    data object Home: AppPages("home")
    data object QrCode: AppPages("qrcode")
}