package io.drakon.uni.ac32007.instagrim.img

import java.awt.image.BufferedImage
import java.awt.image.LookupOp
import java.awt.image.ShortLookupTable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Image inverter filter based on http://jonathangiles.net/blog/?p=702
 */
object InvertImg : ImgFilter {

    val invertLookup: LookupOp

    init {
        val invertTable = Array<Short>(256, { 0 })
        for (i in 0..255) {
            invertTable[i] = (255 - i).toShort()
        }

        invertLookup = LookupOp(ShortLookupTable(0, invertTable.toShortArray()), null)
    }

    /**
     * Apply this filter to the given image (in ByteArray form)
     *
     * @param imgBytes Raw bytes for input image
     * @param type The file type to pass to ImageIO
     * @return The processed image
     */
    override fun process(imgBytes: ByteArray, type: String): ByteArray {
        val img = ImageIO.read(ByteArrayInputStream(imgBytes))
        val dst = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
        invertLookup.filter(img, dst)
        return ByteArrayOutputStream().use {
            ImageIO.write(dst, type, it)
            it.flush()
            it.toByteArray()
        }
    }

}