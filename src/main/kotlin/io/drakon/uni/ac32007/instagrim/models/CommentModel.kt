package io.drakon.uni.ac32007.instagrim.models

import io.drakon.uni.ac32007.instagrim.lib.db.CachedStatement
import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import io.drakon.uni.ac32007.instagrim.stores.Comment
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Database lookups involving comments.
 */
object CommentModel {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val cqlGetCommentsForPic = CachedStatement("SELECT user, time, comment FROM instagrim.comments WHERE picid = ?")
    private val cqlAddCommentToPic = CachedStatement("INSERT INTO instagrim.comments (picid, user, time, comment) VALUES (?,?,?,?)")

    fun getCommentsForPic(pic: UUID): List<Comment> {
        val s = Cassandra.getSession()
        val ps = cqlGetCommentsForPic(s)
        val rs = s.execute(ps.bind(pic))
        val list = mutableListOf<Comment>()

        if (rs.isExhausted) {
            log.debug("No comments for pic {}", pic)
        } else {
            for (row in rs) {
                val user = row.getString("user")
                val time = row.getTimestamp("time")
                val comment = row.getString("comment")
                list.add(Comment(pic, user, time, comment))
            }
        }
        return list.toList() // Make immutable
    }

    fun addComment(comment: Comment) {
        log.debug("Adding comment: {}", comment)
        val s = Cassandra.getSession()
        val ps = cqlAddCommentToPic(s)
        s.execute(ps.bind(comment.pic, comment.user, comment.time, comment.comment))
    }

}