package io.drakon.uni.ac32007.instagrim.img

import org.imgscalr.Scalr
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Filter to produce thumbnails.
 */
object ThumbImg: ImgFilter {

    override fun process(imgBytes: ByteArray, type: String): ByteArray {
        val image = ImageIO.read(ByteArrayInputStream(imgBytes))
        val newImg = Scalr.resize(image, Scalr.Method.SPEED, 250, Scalr.OP_ANTIALIAS, Scalr.OP_GRAYSCALE)
        return ByteArrayOutputStream().use {
            ImageIO.write(newImg, type, it)
            it.flush()
            it.toByteArray()
        }
    }

}