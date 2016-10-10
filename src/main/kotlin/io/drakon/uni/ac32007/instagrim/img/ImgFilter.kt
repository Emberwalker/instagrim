package io.drakon.uni.ac32007.instagrim.img

/**
 * Base interface for image filters.
 *
 * @author Robert T.
 */
interface ImgFilter {

    /**
     * Apply this filter to the given image (in ByteArray form)
     *
     * @param imgBytes Raw bytes for input image
     * @param type The file type to pass to ImageIO
     * @return The processed image
     */
    fun process(imgBytes: ByteArray, type: String): ByteArray

}