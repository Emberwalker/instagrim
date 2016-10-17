package io.drakon.uni.ac32007.instagrim.lib

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Misc. utility methods for servlets/JSP
 */
object ServletUtils {

    /**
     * Sends a client redirect within the current container context.
     */
    fun redirectInContext(request: HttpServletRequest, response: HttpServletResponse, path: String) {
        response.sendRedirect(getPathForHTML(request, path))
    }

    /**
     * Returns the given path with the container context prepended.
     */
    fun getPathForHTML(request: HttpServletRequest, path: String): String {
        val ctx = request.contextPath
        return "$ctx$path"
    }

}