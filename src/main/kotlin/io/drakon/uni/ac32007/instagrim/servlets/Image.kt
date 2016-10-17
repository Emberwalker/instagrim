package io.drakon.uni.ac32007.instagrim.servlets

import com.datastax.driver.core.Cluster
import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import io.drakon.uni.ac32007.instagrim.lib.Convertors
import io.drakon.uni.ac32007.instagrim.models.PicModel
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.PrintWriter
import java.util.*
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(urlPatterns = arrayOf("/Image", "/Image/*", "/Thumb/*", "/Images", "/Images/*"))
@MultipartConfig
class Image : HttpServlet() {

    private val CommandsMap = HashMap<String, Int>()
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1)
        CommandsMap.put("Images", 2)
        CommandsMap.put("Thumb", 3)

    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val args = Convertors.SplitRequestPath(request)
        val command: Int
        try {
            command = CommandsMap.get(args[1]) as Int
        } catch (et: Exception) {
            error("Bad Operator", response)
            return
        }

        when (command) {
            1 -> DisplayImage(Convertors.DISPLAY.PROCESSED, args[2], response)
            2 -> DisplayImageList(args[2], request, response)
            3 -> DisplayImage(Convertors.DISPLAY.THUMB, args[2], response)
            else -> error("Bad Operator", response)
        }
    }

    @Throws(ServletException::class, IOException::class)
    private fun DisplayImageList(User: String, request: HttpServletRequest, response: HttpServletResponse) {
        val tm = PicModel()
        val lsPics = tm.getPicsForUser(User)
        val rd = request.getRequestDispatcher("/WEB-INF/UsersPics.jsp")
        request.setAttribute("Pics", lsPics)
        rd.forward(request, response)

    }

    @Throws(ServletException::class, IOException::class)
    private fun DisplayImage(type: Convertors.DISPLAY, Image: String, response: HttpServletResponse) {
        val tm = PicModel()


        val p = tm.getPic(type, java.util.UUID.fromString(Image))

        val out = response.outputStream

        response.contentType = p!!.type
        response.setContentLength(p.length)
        //out.write(Image);
        val `is` = ByteArrayInputStream(p.bytes)
        val input = BufferedInputStream(`is`)
        val buffer = ByteArray(8192)
        while (input.available() > 0) {
            val length = input.read(buffer)
            out.write(buffer, 0, length)
        }
        out.close()
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        for (part in request.parts) {
            log.debug("Part Name {}", part.name)

            val type = part.contentType
            val filename = part.submittedFileName


            val `is` = request.getPart(part.name).inputStream
            val i = `is`.available()
            val session = request.session
            val lg = session.getAttribute("LoggedIn") as LoggedIn
            var username = "majed"
            if (lg.loggedIn) {
                username = lg.username!! // FIXME: Nullable?
            }
            if (i > 0) {
                val b = ByteArray(i + 1)
                `is`.read(b)
                log.debug("Length: {}", b.size)
                val tm = PicModel()
                tm.insertPic(b, type, filename, username)

                `is`.close()
            }
            val rd = request.getRequestDispatcher("/WEB-INF/upload.jsp")
            rd.forward(request, response)
        }

    }

    @Throws(ServletException::class, IOException::class)
    private fun error(mess: String, response: HttpServletResponse) {

        val out = PrintWriter(response.outputStream)
        out.println("<h1>You have a na error in your input</h1>")
        out.println("<h2>$mess</h2>")
        out.close()
        return
    }

    companion object {
        private val serialVersionUID = 1L
    }
}