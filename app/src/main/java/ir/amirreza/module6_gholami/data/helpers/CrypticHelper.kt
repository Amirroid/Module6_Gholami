package ir.amirreza.module6_gholami.data.helpers

import android.content.Context
import android.os.Build
import android.os.FileUtils
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CrypticHelper {

    val key = generateKey()

    private fun generateKey() = KeyGenerator.getInstance(
        ALGORITHM
    ).apply { init(256) }.generateKey()


    private fun decrypt(encrypted: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ByteArray(16)))
        }
        return cipher.doFinal(encrypted)
    }


    fun getStringKey() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getEncoder().encodeToString(key.encoded) ?: "'"
    } else {
        ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSecretKey(key: String): SecretKey {
        val decodeKey = Base64.getDecoder().decode(key)
        return SecretKeySpec(decodeKey, 0, decodeKey.size, "AES")
    }

    fun decryptFile(file: File, secretKey: SecretKey) {
        val fis = FileInputStream(file)
        val encrypted = decrypt(fis.readBytes(), secretKey)
        val fos = FileOutputStream(file)
        fos.write(encrypted, 0, encrypted.size)
    }

    fun encrypt(input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(ByteArray(16)))
        }
        return cipher.doFinal(input)
    }

    fun encryptFile(file: File) {
        val fis = FileInputStream(file)
        val encrypted = encrypt(fis.readBytes())
        val fos = FileOutputStream(file)
        fos.write(encrypted, 0, encrypted.size)
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}