package io.drakon.uni.ac32007.instagrim.servlets

import io.drakon.uni.ac32007.instagrim.lib.ext.redirectInContext
import io.drakon.uni.ac32007.instagrim.models.User
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import org.slf4j.LoggerFactory
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "Login", urlPatterns = arrayOf("/Login", "/Login/*"))
class Login : HttpServlet() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {

        val username = request.getParameter("username")
        val password = request.getParameter("password")

        val us = User()
        val isValid = us.IsValidUser(username, password)
        val session = request.session
        log.info("Session in servlet: {}", session)
        if (isValid) {
            val lg = LoggedIn(true, username)
            session.setAttribute("LoggedIn", lg)
            log.info("Session in servlet: {}", session)
            response.redirectInContext(request, "/")
        } else {
            response.redirectInContext(request, "/Login")
        }

    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val rd = req.getRequestDispatcher("/WEB-INF/login.jsp")
        rd.forward(req, resp)
    }

    override fun getServletInfo(): String {
        return "Login Controller"
    }

}
