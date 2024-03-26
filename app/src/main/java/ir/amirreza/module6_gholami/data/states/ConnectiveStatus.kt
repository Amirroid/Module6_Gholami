package ir.amirreza.module6_gholami.data.states

sealed class ConnectiveStatus(
    val message: String
) {
    data object Success : ConnectiveStatus("Success")
    data class Error(val e: String) : ConnectiveStatus(e)
}