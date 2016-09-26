package io.drakon.uni.ac32007.instagrim.lib

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object AeSimpleSHA1 {

    private fun convertToHex(data: ByteArray): String {
        val buf = StringBuffer()
        for (i in data.indices) {
            // TODO: Test this works; had to add toInt
            var halfbyte = data[i].toInt().ushr(4) and 0x0F
            var two_halfs = 0
            do {
                if (0 <= halfbyte && halfbyte <= 9)
                    buf.append(('0' + halfbyte).toChar())
                else
                    buf.append(('a' + (halfbyte - 10)).toChar())
                // TODO: Test this works; had to add toInt
                halfbyte = data[i].toInt() and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun SHA1(text: String): String {
        val md: MessageDigest
        md = MessageDigest.getInstance("SHA-1")
        md.update(text.toByteArray(charset("iso-8859-1")), 0, text.length)
        val sha1hash = md.digest()
        return convertToHex(sha1hash)
    }
} 
 