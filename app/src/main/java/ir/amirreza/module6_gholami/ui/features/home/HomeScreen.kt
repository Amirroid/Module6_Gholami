package ir.amirreza.module6_gholami.ui.features.home

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import ir.amirreza.module6_gholami.R
import ir.amirreza.module6_gholami.data.helpers.CrypticHelper
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.checkPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var time by remember {
        mutableLongStateOf(0L)
    }
    var timeAdd by remember {
        mutableLongStateOf(0)
    }
    var sendEnable by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = timeAdd) {
        if (timeAdd != 0L) {
            while (time < 60000) {
                delay(1.seconds)
                time += timeAdd
            }
        }
    }

    val permission = remember {
        context.checkPermission(Manifest.permission.RECORD_AUDIO)
    }

    val file = remember {
        File(context.cacheDir, "${System.currentTimeMillis()}.mp3").apply {
            if (exists().not()) {
                createNewFile()
            }
        }
    }
    var recorder by remember {
        mutableStateOf<MediaRecorder?>(null)
    }
    val crypticHelper = remember {
        CrypticHelper()
    }

    fun record() {
        recorder?.start()
    }

    fun pause() {
        recorder?.pause()
    }

    fun stop() {
        recorder?.stop()
        recorder?.release()
    }

    val appState = LocaleAppState.current
    DisposableEffect(key1 = Unit) {
        if (permission) {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(file.path)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                try {
                    prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        onDispose {
            stop()
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (permission.not()) {
            (context as FragmentActivity).requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        IconButton(onClick = {}, modifier = Modifier.align(Alignment.Start)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .drawBehind {
                    drawArc(
                        Color(0xFFC2185B),
                        startAngle = -90f,
                        time / 60000f * 360,
                        false,
                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color(0xFFC2185B))
                .size(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (permission.not()) return@detectTapGestures
                            sendEnable = false
                            timeAdd = 1000
                            record()
                        },
                    ) {
                        if (permission.not()) return@detectTapGestures
                        timeAdd = 0
                        sendEnable = true
                        pause()
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                contentDescription = null,
                tint = Color.White
            )
        }
        Button(onClick = {
            time = 0
            stop()
            appState.scope.launch {
                crypticHelper.encryptFile(context, file)
            }
        }, enabled = sendEnable, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Send")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}