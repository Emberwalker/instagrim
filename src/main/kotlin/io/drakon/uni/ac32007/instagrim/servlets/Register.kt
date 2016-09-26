package io.drakon.uni.ac32007.instagrim.servlets

import com.datastax.driver.core.Cluster
import java.io.IOException
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import io.drakon.uni.ac32007.instagrim.lib.CassandraHosts
import io.drakon.uni.ac32007.instagrim.models.User

@WebServlet(name = "Register", urlPatterns = arrayOf("/Register"))
class Register : HttpServlet() {

    lateinit internal var cluster: Cluster

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
        us.setCluster(cluster)
        us.RegisterUser(username, password)

        response.sendRedirect("/Instagrim") // FIXME: Make relative to container root.

    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    override fun getServletInfo(): String {
        return "Short description" // FIXME: Change this.
    }

}
