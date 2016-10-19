package io.drakon.uni.ac32007.instagrim.models

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import io.drakon.uni.ac32007.instagrim.img.DecolourImg
import io.drakon.uni.ac32007.instagrim.img.InvertImg
import io.drakon.uni.ac32007.instagrim.img.ThumbImg
import io.drakon.uni.ac32007.instagrim.lib.Convertors
import io.drakon.uni.ac32007.instagrim.lib.db.CachedStatement
import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import io.drakon.uni.ac32007.instagrim.stores.Pic
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*

class PicModel {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        // CQL statements must be in the companion, to avoid being recomputed by each thread in the app server.
        // INSERT
        private val cqlInsertPic = CachedStatement("insert into pics ( picid, image,thumb, processed, user, interaction_time, imagelength, thumblength, processedlength, type, name, inverted, invertedlength) values(?,?,?,?,?,?,?,?,?,?,?,?,?)")
        private val cqlInsertPicToUser = CachedStatement("insert into userpiclist ( picid, user, pic_added) values(?,?,?)")

        // SELECT
        private val cqlUserPicListByUser = CachedStatement("select picid from userpiclist where user =?")
        private val cqlDisplay = mapOf(
                Convertors.DISPLAY.IMAGE to CachedStatement("select image,imagelength,type from pics where picid =?"),
                Convertors.DISPLAY.THUMB to CachedStatement("select thumb,imagelength,thumblength,type from pics where picid =?"),
                Convertors.DISPLAY.PROCESSED to CachedStatement("select processed,processedlength,type from pics where picid =?"),
                Convertors.DISPLAY.INVERTED to CachedStatement("select inverted,invertedlength,type from pics where picid =?")
        )
    }

    fun insertPic(b: ByteArray, type: String, name: String, user: String) {
        val types = Convertors.SplitFiletype(type)
        val buffer = ByteBuffer.wrap(b)
        val length = b.size
        val picid = Convertors.timeUUID

        val thumbbuf = ByteBuffer.wrap(ThumbImg.process(b, types[1]))
        val thumblength = thumbbuf.array().size
        val processedbuf = ByteBuffer.wrap(DecolourImg.process(b, types[1]))
        val processedlength = processedbuf.array().size
        val invertedbuf = ByteBuffer.wrap(InvertImg.process(b, types[1]))
        val invertedlength = invertedbuf.array().size

        val session = Cassandra.getSession()
        val bsInsertPic = BoundStatement(cqlInsertPic.get(session))
        val bsInsertPicToUser = BoundStatement(cqlInsertPicToUser.get(session))

        val DateAdded = Date()
        session.execute(bsInsertPic.bind(picid, buffer, thumbbuf, processedbuf, user, DateAdded, length, thumblength, processedlength, type, name, invertedbuf, invertedlength))
        session.execute(bsInsertPicToUser.bind(picid, user, DateAdded))
    }

    fun getPicsForUser(User: String): java.util.LinkedList<Pic>? {
        val Pics = java.util.LinkedList<Pic>()
        val session = Cassandra.getSession()
        val boundStatement = BoundStatement(cqlUserPicListByUser.get(session))
        val rs = session.execute(// this is where the query is executed
                boundStatement.bind(// here you are binding the 'boundStatement'
                        User))
        if (rs.isExhausted) {
            log.warn("No Images returned")
            return null
        } else {
            for (row in rs) {
                val pic = Pic()
                val UUID = row.getUUID("picid")
                pic.setUUID(UUID)
                Pics.add(pic)

            }
        }
        return Pics
    }

    fun getPic(image_type: Convertors.DISPLAY, picid: java.util.UUID): Pic? {
        val session = Cassandra.getSession()
        var bImage: ByteBuffer? = null
        var type: String? = null
        var length = 0
        try {
            val ps: PreparedStatement = cqlDisplay[image_type]!!.get(session)
            val boundStatement = BoundStatement(ps)
            val rs = session.execute(// this is where the query is executed
                    boundStatement.bind(// here you are binding the 'boundStatement'
                            picid))

            if (rs!!.isExhausted) {
                log.warn("No Images returned")
                return null
            } else {
                for (row in rs) {
                    when (image_type) {
                        Convertors.DISPLAY.IMAGE -> {
                            bImage = row.getBytes("image")
                            length = row.getInt("imagelength")
                        }
                        Convertors.DISPLAY.THUMB -> {
                            bImage = row.getBytes("thumb")
                            length = row.getInt("thumblength")
                        }
                        Convertors.DISPLAY.PROCESSED -> {
                            bImage = row.getBytes("processed")
                            length = row.getInt("processedlength")
                        }
                        Convertors.DISPLAY.INVERTED -> {
                            bImage = row.getBytes("inverted")
                            length = row.getInt("invertedlength")
                        }
                    }

                    type = row.getString("type")
                }
            }
        } catch (et: Exception) {
            log.error("Error getting Pic", et)
            return null
        }

        val p = Pic()
        p.setPic(bImage!!, length, type!!)

        return p

    }

}
