package io.drakon.uni.ac32007.instagrim.img

import org.imgscalr.Scalr
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * The classic Instagrim filter - decolour. Ported from the original code in Instagrim (loosely).
 */
object DecolourImg : ImgFilter {

    override fun process(imgBytes: ByteArray, type: String): ByteArray {
        val image = ImageIO.read(ByteArrayInputStream(imgBytes))
        val newImg = Scalr.apply(image, Scalr.OP_GRAYSCALE)
        return ByteArrayOutputStream().use {
            ImageIO.write(newImg, type, it)
            it.flush()
            it.toByteArray()
        }
    }

}