package io.drakon.uni.ac32007.instagrim.lib

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.xml.bind.DatatypeConverter

object SimpleSHA1 {

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun SHA1(text: String): String {
        val md: MessageDigest
        md = MessageDigest.getInstance("SHA-1")
        md.update(text.toByteArray(charset("iso-8859-1")), 0, text.length)
        val sha1hash = md.digest()
        // Must lower-case to maintain compatibility with original implementation
        return DatatypeConverter.printHexBinary(sha1hash).toLowerCase(locale = Locale("iso-8859-1"))
    }

} 
 