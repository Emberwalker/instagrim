package io.drakon.uni.ac32007.instagrim.models

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Date
import javax.imageio.ImageIO
import org.imgscalr.Scalr.*
import org.slf4j.LoggerFactory

import io.drakon.uni.ac32007.instagrim.lib.Convertors
import io.drakon.uni.ac32007.instagrim.stores.Pic

class PicModel {

    lateinit internal var cluster: Cluster
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun setCluster(cluster: Cluster) {
        this.cluster = cluster
    }

    fun insertPic(b: ByteArray, type: String, name: String, user: String) {
        try {

            val types = Convertors.SplitFiletype(type)
            val buffer = ByteBuffer.wrap(b)
            val length = b.size
            val picid = Convertors.timeUUID

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            // TODO: UNIX only, make work elsewhere
            val success = File("/var/tmp/instagrim/").mkdirs()
            val output = FileOutputStream(File("/var/tmp/instagrim/" + picid))

            output.write(b)
            val thumbb = picresize(picid.toString(), types[1])
            val thumblength = thumbb!!.size
            val thumbbuf = ByteBuffer.wrap(thumbb)
            val processedb = picdecolour(picid.toString(), types[1])
            val processedbuf = ByteBuffer.wrap(processedb!!)
            val processedlength = processedb.size
            val session = cluster.connect("instagrim")

            val psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)")
            val psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)")
            val bsInsertPic = BoundStatement(psInsertPic)
            val bsInsertPicToUser = BoundStatement(psInsertPicToUser)

            val DateAdded = Date()
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf, processedbuf, user, DateAdded, length, thumblength, processedlength, type, name))
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded))
            session.close()

        } catch (ex: IOException) {
            log.error("I/O Error during insertPic", ex)
        }

    }

    fun picresize(picid: String, type: String): ByteArray? {
        try {
            val BI = ImageIO.read(File("/var/tmp/instagrim/" + picid))
            val thumbnail = createThumbnail(BI)
            val baos = ByteArrayOutputStream()
            ImageIO.write(thumbnail, type, baos)
            baos.flush()

            val imageInByte = baos.toByteArray()
            baos.close()
            return imageInByte
        } catch (ex: IOException) {
            log.error("I/O Error during picresize", ex)
        }

        return null
    }

    fun picdecolour(picid: String, type: String): ByteArray? {
        try {
            val BI = ImageIO.read(File("/var/tmp/instagrim/" + picid))
            val processed = createProcessed(BI)
            val baos = ByteArrayOutputStream()
            ImageIO.write(processed, type, baos)
            baos.flush()
            val imageInByte = baos.toByteArray()
            baos.close()
            return imageInByte
        } catch (ex: IOException) {
            log.error("I/O Error during pixdecolour", ex)
        }

        return null
    }

    fun getPicsForUser(User: String): java.util.LinkedList<Pic>? {
        val Pics = java.util.LinkedList<Pic>()
        val session = cluster.connect("instagrim")
        val ps = session.prepare("select picid from userpiclist where user =?")
        var rs: ResultSet? = null
        val boundStatement = BoundStatement(ps)
        rs = session.execute(// this is where the query is executed
                boundStatement.bind(// here you are binding the 'boundStatement'
                        User))
        if (rs!!.isExhausted) {
            log.warn("No Images returned")
            return null
        } else {
            for (row in rs) {
                val pic = Pic()
                val UUID = row.getUUID("picid")
                println("UUID" + UUID.toString())
                pic.setUUID(UUID)
                Pics.add(pic)

            }
        }
        return Pics
    }

    fun getPic(image_type: Convertors.DISPLAY, picid: java.util.UUID): Pic? {
        val session = cluster.connect("instagrim")
        var bImage: ByteBuffer? = null
        var type: String? = null
        var length = 0
        try {
            val ps: PreparedStatement = session.prepare(when (image_type) {
                Convertors.DISPLAY.IMAGE -> "select image,imagelength,type from pics where picid =?"
                Convertors.DISPLAY.THUMB -> "select thumb,imagelength,thumblength,type from pics where picid =?"
                Convertors.DISPLAY.PROCESSED -> "select processed,processedlength,type from pics where picid =?"
            })
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
                    }

                    type = row.getString("type")
                }
            }
        } catch (et: Exception) {
            log.error("Error getting Pic", et)
            return null
        }

        session.close()
        val p = Pic()
        p.setPic(bImage!!, length, type!!)

        return p

    }

    companion object {

        fun createThumbnail(img: BufferedImage): BufferedImage {
            var img = img
            img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE)
            // Let's add a little border before we return result.
            return pad(img, 2)
        }

        fun createProcessed(img: BufferedImage): BufferedImage {
            var img = img
            val Width = img.width - 1
            img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE)
            return pad(img, 4)
        }
    }

}
