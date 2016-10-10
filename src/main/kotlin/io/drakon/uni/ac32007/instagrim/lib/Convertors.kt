package io.drakon.uni.ac32007.instagrim.lib

import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.util.*
//import java.util.UUID;
import javax.servlet.http.HttpServletRequest

object Convertors {

    enum class DISPLAY(val id: Int) {
        IMAGE(0),
        THUMB(1),
        PROCESSED(2)
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    val timeUUID: java.util.UUID
        get() = java.util.UUID.fromString(com.eaio.uuid.UUID().toString())

    fun asByteArray(uuid: java.util.UUID): ByteArray {

        val msb = uuid.mostSignificantBits
        val lsb = uuid.leastSignificantBits
        val buffer = ByteArray(16)

        for (i in 0..7) {
            buffer[i] = msb.ushr(8 * (7 - i)).toByte()
        }
        for (i in 8..15) {
            buffer[i] = lsb.ushr(8 * (7 - i)).toByte()
        }

        return buffer
    }

    fun longToByteArray(value: Long): ByteArray {
        var value = value
        val buffer = ByteArray(8) //longs are 8 bytes I believe
        for (i in 7 downTo 0) { //fill from the right
            buffer[i] = (value and 0x00000000000000ff).toByte() //get the bottom byte

            value = value.ushr(8) //Shift the value right 8 bits
        }
        return buffer
    }

    fun byteArrayToLong(buffer: ByteArray): Long {
        var value: Long = 0
        var multiplier: Long = 1
        for (i in 7 downTo 0) { //get from the right

            value += (buffer[i].toInt() and 0xff) * multiplier // add the value * the hex mulitplier
            multiplier = multiplier shl 8
        }
        return value
    }

    fun displayByteArrayAsHex(buffer: ByteArray) {
        val byteArrayLength = buffer.size
        for (i in 0..byteArrayLength - 1) {
            val `val` = buffer[i].toInt()
        }

    }

    //From: http://www.captain.at/howto-java-convert-binary-data.php
    fun arr2long(arr: ByteArray, start: Int): Long {
        var i = 0
        val len = 4
        var cnt = 0
        val tmp = ByteArray(len)
        i = start
        while (i < start + len) {
            tmp[cnt] = arr[i]
            cnt++
            i++
        }
        var accum: Long = 0
        i = 0
        var shiftBy = 0
        while (shiftBy < 32) {
            accum = accum or ((tmp[i].toInt() and 0xff).toLong() shl shiftBy)
            i++
            shiftBy += 8
        }
        return accum
    }

    fun SplitTags(Tags: String): Array<String> {
        val st = Convertors.SplitTagString(Tags)
        val args = Array<String>(st.countTokens() + 1, init = { "" })  //+1 for _No_Tag_
        //Lets assume the number is the last argument

        var argv = 0
        while (st.hasMoreTokens()) {
            args[argv] = String()
            args[argv] = st.nextToken()
            argv++
        }
        args[argv] = "_No-Tag_"
        return args
    }

    private fun SplitTagString(str: String): StringTokenizer {
        return StringTokenizer(str, ",")

    }

    fun SplitFiletype(type: String): Array<String> {
        val st = SplitString(type)
        val args = Array<String>(st.countTokens(), init = { "" })
        //Lets assume the number is the last argument

        var argv = 0
        while (st.hasMoreTokens()) {
            args[argv] = String()

            args[argv] = st.nextToken()
            try {
                args[argv] = URLDecoder.decode(args[argv], "UTF-8")

            } catch (et: Exception) {
                log.error("Bad URL Encoding", et)
            }

            argv++
        }

        //so now they'll be in the args array.
        // argv[0] should be the user directory
        return args
    }

    fun SplitRequestPath(request: HttpServletRequest): Array<String> {
        val st = SplitString(request.requestURI)
        val args = Array<String>(st.countTokens(), init = { "" })
        //Lets assume the number is the last argument

        var argv = 0
        while (st.hasMoreTokens()) {
            args[argv] = String()

            args[argv] = st.nextToken()
            try {
                args[argv] = URLDecoder.decode(args[argv], "UTF-8")

            } catch (et: Exception) {
                log.error("Bad URL Encoding", et)
            }

            argv++
        }

        //so now they'll be in the args array.
        // argv[0] should be the user directory
        return args
    }

    private fun SplitString(str: String): StringTokenizer {
        return StringTokenizer(str, "/")

    }

}
