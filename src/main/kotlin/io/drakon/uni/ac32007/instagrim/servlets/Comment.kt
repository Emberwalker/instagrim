package io.drakon.uni.ac32007.instagrim.servlets

import io.drakon.uni.ac32007.instagrim.lib.ext.redirectInContext
import io.drakon.uni.ac32007.instagrim.models.CommentModel
import io.drakon.uni.ac32007.instagrim.stores.Comment
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.util.*
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Comments servlet.
 */
@WebServlet(urlPatterns = arrayOf("/Comment"))
@MultipartConfig
class Comment: HttpServlet() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val uuid = req.getParameter("pic")
        val comment = req.getParameter("comment")
        val date = Date()

        val lg = req.session.getAttribute("LoggedIn") as LoggedIn?
        var loggedIn = false
        var username = ""
        if (lg != null && lg.loggedIn) {
            loggedIn = true
            username = lg.username!!
        }

        if (!loggedIn) {
            log.warn("Attempt to add comment while not logged in.")
            error(resp, 401, "Not logged in.")
            return
        }

        if (uuid !is String || uuid == "" || comment !is String || comment == "") {
            log.warn("Malformed form input.")
            error(resp, 400, "Invalid input format.")
            return
        }

        val comm = Comment(UUID.fromString(uuid), username, date, comment)
        log.debug("Submitting: {}", comm)
        CommentModel.addComment(comm)

        resp.redirectInContext(req, "/Image/$uuid")
    }

    private fun error(resp: HttpServletResponse, code: Int, msg: String) {
        resp.status = code
        val out = PrintWriter(resp.outputStream)
        out.println("<h1>An error has occurred.</h1>")
        out.println("<h2>$msg</h2>")
        out.close()
    }

}