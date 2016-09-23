/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.drakon.uni.ac32007.instagrim.stores

import com.datastax.driver.core.utils.Bytes
import java.nio.ByteBuffer

class Pic {

    var buffer: ByteBuffer? = null
        private set
    var length: Int = 0
        private set
    var type: String? = null
        private set
    private var UUID: java.util.UUID? = null

    fun Pic() {

    }

    fun setUUID(UUID: java.util.UUID) {
        this.UUID = UUID
    }

    val suuid: String
        get() = UUID!!.toString()

    fun setPic(bImage: ByteBuffer, length: Int, type: String) {
        this.buffer = bImage
        this.length = length
        this.type = type
    }

    val bytes: ByteArray
        get() {

            val image = Bytes.getArray(buffer!!)
            return image
        }

}
