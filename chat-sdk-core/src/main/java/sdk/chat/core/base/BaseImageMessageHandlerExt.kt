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

@JvmOverloads
fun compressImage(file: File, maxImageSize: Int = 600): File {
    val context = ChatSDK.ctx()

    return runBlocking {
        Compressor.compress(context, file) {
            quality(80)
            format(Bitmap.CompressFormat.JPEG)

            file.inputStream().use { input ->
                val exif = ExifInterface(input)
                val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
                val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)

                if (width < maxImageSize || height < maxImageSize) {
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
}