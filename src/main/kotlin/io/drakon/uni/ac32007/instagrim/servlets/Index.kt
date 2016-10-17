package io.drakon.uni.ac32007.instagrim.servlets

import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Site index servlet
 */
@WebServlet(urlPatterns = arrayOf("/index", "/"))
class Index : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val lg = req.session.getAttribute("LoggedIn") as LoggedIn?
        var loggedIn = false
        if (lg != null && lg.loggedIn) {
            loggedIn = true
            req.setAttribute("username", lg.username!!)
        }
        req.setAttribute("loggedIn", loggedIn)
        val rd = req.getRequestDispatcher("/WEB-INF/index.jsp")
        rd.forward(req, resp)
    }

}