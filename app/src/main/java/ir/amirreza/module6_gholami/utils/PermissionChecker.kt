package ir.amirreza.module6_gholami.utils

import android.content.Context
import android.content.pm.PackageManager

fun Context.checkPermission(
    permission: String
) = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED