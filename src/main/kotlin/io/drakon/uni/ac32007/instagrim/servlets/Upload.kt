package io.drakon.uni.ac32007.instagrim.servlets

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Facade around upload.jsp to make uniform URLs.
 */
@WebServlet(name = "Upload", urlPatterns = arrayOf("/Upload"))
class Upload: HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val rd = req.getRequestDispatcher("/WEB-INF/upload.jsp")
        rd.forward(req, resp)
    }

}