package ir.amirreza.module6_gholami.utils

sealed class AppPages(val route: String) {
    data object Register: AppPages("register")
    data object Home: AppPages("home")
    data object QrCodeScanner: AppPages("QrCodeScanner")
    data object QrCode: AppPages("qrcode")
    data object Devices: AppPages("devices")
    data object SendFile: AppPages("send_file")
    data object ReceiveFile: AppPages("receive_file")
}