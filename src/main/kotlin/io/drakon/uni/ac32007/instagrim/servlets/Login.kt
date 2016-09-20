package io.drakon.uni.ac32007.instagrim.servlets

import com.datastax.driver.core.Cluster
import java.io.IOException
import javax.servlet.RequestDispatcher
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import io.drakon.uni.ac32007.instagrim.lib.CassandraHosts
import io.drakon.uni.ac32007.instagrim.models.User
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn

@WebServlet(name = "Login", urlPatterns = arrayOf("/Login", "/Login/*"))
class Login : HttpServlet() {

    // FIXME: Make this not nullable.
    internal var cluster: Cluster? = null

    @Throws(ServletException::class)
    override fun init(config: ServletConfig) {
        cluster = CassandraHosts.getCluster()
    }

    /**
     * Handles the HTTP `POST` method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {

        val username = request.getParameter("username")
        val password = request.getParameter("password")

        val us = User()
        us.setCluster(cluster!!)
        val isValid = us.IsValidUser(username, password)
        val session = request.session
        println("Session in servlet " + session)
        if (isValid) {
            val lg = LoggedIn()
            lg.setLogedin()
            lg.username = username
            //request.setAttribute("LoggedIn", lg);

            session.setAttribute("LoggedIn", lg)
            println("Session in servlet " + session)
            val rd = request.getRequestDispatcher("index.jsp")
            rd.forward(request, response)

        } else {
            response.sendRedirect("/Instagrim/login.jsp") // FIXME: Make relative to container
        }

    }

    /**
     * Returns a short description of the servlet.

     * @return a String containing servlet description
     */
    override fun getServletInfo(): String {
        return "Short description" // FIXME: Change this.
    }

}
