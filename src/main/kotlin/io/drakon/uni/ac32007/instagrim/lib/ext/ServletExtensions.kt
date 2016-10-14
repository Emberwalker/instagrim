package io.drakon.uni.ac32007.instagrim.lib.ext

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Extension methods to various Java classes involved in servlets.
 *
 * @author Robert T.
 */

/**
 * Redirects to a page in the current context/container.
 *
 * @param request The servlet request object
 * @param path The path to go to within the container
 */
fun HttpServletResponse.redirectInContext(request: HttpServletRequest, path: String) {
    val ctxPath = request.contextPath
    this.sendRedirect("$ctxPath$path")
}