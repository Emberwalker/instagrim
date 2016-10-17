package io.drakon.uni.ac32007.instagrim.filters

import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@WebFilter(filterName = "ProtectPages", urlPatterns = arrayOf("/upload.jsp", "/Upload"), dispatcherTypes = arrayOf(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE))
class ProtectPages : Filter {

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private var filterConfig: FilterConfig? = null
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {

        log.debug("doFilter")

        log.debug("Doing filter")
        val httpReq = request as HttpServletRequest
        val session = httpReq.getSession(false)
        val li = session.getAttribute("LoggedIn") as LoggedIn?
        log.debug("Session in filter " + session)
        if (li == null || li.loggedIn == false) {
            log.debug("Foward to login")
            val rd = request.getRequestDispatcher("/Login")
            rd.forward(request, response)
        }
        var problem: Throwable? = null
        try {
            chain.doFilter(request, response)
        } catch (t: Throwable) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t
            log.error("Caught throwable from filter chain", t)
        }

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem is ServletException) {
                throw problem
            }
            if (problem is IOException) {
                throw problem
            }
            sendProcessingError(problem, response)
        }
    }

    /**
     * Destroy method for this filter
     */
    override fun destroy() {
    }

    /**
     * Init method for this filter
     */
    override fun init(filterConfig: FilterConfig?) {
        this.filterConfig = filterConfig
        if (filterConfig != null) {
            log.debug("ProtectPages:Initializing filter")
        }
    }

    /**
     * Return a String representation of this object.
     */
    override fun toString(): String {
        if (filterConfig == null) {
            return "ProtectPages()"
        }
        val sb = StringBuffer("ProtectPages(")
        sb.append(filterConfig)
        sb.append(")")
        return sb.toString()
    }

    private fun sendProcessingError(t: Throwable, response: ServletResponse) {
        val stackTrace = getStackTrace(t)

        if (stackTrace != null && stackTrace != "") {
            try {
                response.contentType = "text/html"
                val ps = PrintStream(response.outputStream)
                val pw = PrintWriter(ps)
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n")
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n")
                pw.print(stackTrace)
                pw.print("</pre></body>\n</html>")
                pw.close()
                ps.close()
                response.outputStream.close()
            } catch (ex: Exception) {
            }

        } else {
            try {
                val ps = PrintStream(response.outputStream)
                t.printStackTrace(ps)
                ps.close()
                response.outputStream.close()
            } catch (ex: Exception) {
            }

        }
    }

    companion object {

        fun getStackTrace(t: Throwable): String? {
            try {
                return StringWriter().use {
                    PrintWriter(it).use {
                        t.printStackTrace(it)
                    }
                    it.buffer.toString()
                }
            } catch (ex: Exception) {
                return null
            }
        }

    }

}
