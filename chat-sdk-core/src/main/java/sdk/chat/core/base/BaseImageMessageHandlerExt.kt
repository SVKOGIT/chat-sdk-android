package sdk.chat.core.base

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.runBlocking
import sdk.chat.core.session.ChatSDK
import java.io.File
import kotlin.math.max

@JvmOverloads
fun compressImage(file: File, maxImageSize: Int = 600): File {
    val context = ChatSDK.ctx()
    val exif = file.exif

    val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
    val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)

    return runBlocking {
        Compressor.compress(context, file) {
            quality(80)
            format(Bitmap.CompressFormat.JPEG)

            if (max(width, height) < maxImageSize || width == 0 || height == 0) {
                return@compress
            }

            if (height > width) {
                resolution(maxImageSize, maxImageSize / width * height)
            } else {
                resolution(maxImageSize / height * width, maxImageSize)
            }
        }
    }
}

private val File.exif
    get() = inputStream().use {
        ExifInterface(it)
    }