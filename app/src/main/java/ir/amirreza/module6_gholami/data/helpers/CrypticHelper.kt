package ir.amirreza.module6_gholami.data.helpers

import android.content.Context
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class CrypticHelper {

    val key = generateKey()

    private fun generateKey() = KeyGenerator.getInstance(
        ALGORITHM
    ).apply { init(256) }.generateKey()


    fun decrypt(encrypted: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key, IvParameterSpec(ByteArray(16)))
        }
        return cipher.doFinal(encrypted)
    }

    fun encrypt(input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(ByteArray(16)))
        }
        return cipher.doFinal(input)
    }

    fun encryptFile(context: Context, file: File): File {
        val fis = FileInputStream(file)
        val encrypted = encrypt(fis.readBytes())
        val encryptedFile = File(context.cacheDir, "e${System.currentTimeMillis()}.mp3")
        encryptedFile.createNewFile()
        val fos = FileOutputStream(encryptedFile)
        fos.write(encrypted, 0, encrypted.size)
        return encryptedFile
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}