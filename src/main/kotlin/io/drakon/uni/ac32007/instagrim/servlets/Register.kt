package io.drakon.uni.ac32007.instagrim.servlets

import com.datastax.driver.core.Cluster
import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import io.drakon.uni.ac32007.instagrim.lib.ext.redirectInContext
import io.drakon.uni.ac32007.instagrim.models.User
import java.io.IOException
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "Register", urlPatterns = arrayOf("/Register"))
class Register : HttpServlet() {

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
        us.RegisterUser(username, password)

        response.redirectInContext(request, "/")
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val rd = req.getRequestDispatcher("/WEB-INF/register.jsp")
        rd.forward(req, resp)
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    override fun getServletInfo(): String {
        return "User registration"
    }

}
