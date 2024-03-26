package ir.amirreza.module6_gholami.utils

import android.view.SurfaceHolder

class NothingSurfaceHolder : SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) = Unit

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

    override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
}