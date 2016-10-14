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

@WebFilter(filterName = "ProtectPages", urlPatterns = arrayOf("/upload.jsp"), dispatcherTypes = arrayOf(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE))
class ProtectPages : Filter {

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private var filterConfig: FilterConfig? = null

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Throws(IOException::class, ServletException::class)
    private fun doBeforeProcessing(request: ServletRequest, response: ServletResponse) {
        log.debug("DoBeforeProcessing")

        // Write code here to process the request and/or response before
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log items on the request object,
        // such as the parameters.
        /*
         for (Enumeration en = request.getParameterNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         String values[] = request.getParameterValues(name);
         int n = values.length;
         StringBuffer buf = new StringBuffer();
         buf.append(name);
         buf.append("=");
         for(int i=0; i < n; i++) {
         buf.append(values[i]);
         if (i < n-1)
         buf.append(",");
         }
         log(buf.toString());
         }
         */
    }

    @Throws(IOException::class, ServletException::class)
    private fun doAfterProcessing(request: ServletRequest, response: ServletResponse) {
        log.debug("DoAfterProcessing")

        // Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log the attributes on the
        // request object after the request has been processed.
        /*
         for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         Object value = request.getAttribute(name);
         log("attribute: " + name + "=" + value.toString());

         }
         */
        // For example, a filter might append something to the response.
        /*
         PrintWriter respOut = new PrintWriter(response.getWriter());
         respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
    }

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

        doBeforeProcessing(request, response)
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

        doAfterProcessing(request, response)

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
     * Return the filter configuration object for this filter.
     */
    fun getFilterConfig(): FilterConfig? {
        return this.filterConfig
    }

    /**
     * Set the filter configuration object for this filter.

     * @param filterConfig The filter configuration object
     */
    fun setFilterConfig(filterConfig: FilterConfig) {
        this.filterConfig = filterConfig
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
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n") //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n")
                pw.print(stackTrace)
                pw.print("</pre></body>\n</html>") //NOI18N
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
            var stackTrace: String? = null
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                pw.close()
                sw.close()
                stackTrace = sw.buffer.toString()
            } catch (ex: Exception) {
            }

            return stackTrace
        }
    }

}
