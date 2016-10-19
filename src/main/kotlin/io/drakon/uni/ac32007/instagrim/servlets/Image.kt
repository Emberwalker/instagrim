package io.drakon.uni.ac32007.instagrim.servlets

import io.drakon.uni.ac32007.instagrim.lib.Convertors
import io.drakon.uni.ac32007.instagrim.lib.ServletUtils
import io.drakon.uni.ac32007.instagrim.models.PicModel
import io.drakon.uni.ac32007.instagrim.stores.LoggedIn
import io.drakon.uni.ac32007.instagrim.stores.Pic
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.PrintWriter
import java.util.*
import javax.servlet.ServletException
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(urlPatterns = arrayOf("/Image", "/Image/*", "/Thumb/*", "/Images", "/Images/*"))
@MultipartConfig
class Image : HttpServlet() {

    private val CommandsMap = mapOf(
            "Image" to 1,
            "Images" to 2,
            "Thumb" to 3
    )
    private val ImageTypes = arrayOf(
            "Greyscale" to Convertors.DISPLAY.PROCESSED,
            //"Inverted" to Convertors.DISPLAY.INVERTED,
            "Raw" to Convertors.DISPLAY.IMAGE
    )
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val numberRegex = Regex("^\\d$")

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
            1 -> DisplayImage(args, request, response)
            2 -> DisplayImageList(args[2], request, response)
            3 -> getThumb(args[2], response)
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

    private fun getThumb(img: String, response: HttpServletResponse) {
        val p = PicModel().getPic(Convertors.DISPLAY.THUMB, UUID.fromString(img))
        if (p is Pic) {
            writeImgToResponse(p, response)
        } else {
            error("No such image", response)
        }
    }

    @Throws(ServletException::class, IOException::class)
    private fun DisplayImage(argArray: Array<String>, request: HttpServletRequest, response: HttpServletResponse) {
        val ctxRemove = request.contextPath.removePrefix("/")
        val _args = argArray.filterNot { it == ctxRemove }
        if (_args.size == 3) {
            // Specific image type
            val displayType = ImageTypes.first { _args[1] == it.first }.second
            if (displayType is Convertors.DISPLAY) {
                val p = PicModel().getPic(displayType, UUID.fromString(_args[2]))
                if (p is Pic) {
                    writeImgToResponse(p, response)
                } else {
                    response.status = 404
                    error("No such image", response)
                }
            } else {
                response.status = 400
                error("Unknown image type", response)
            }
        } else if (_args.size == 2) {
            // Image page
            var imgType = 0
            val picUUID = _args[1]

            if (PicModel().getPic(Convertors.DISPLAY.THUMB, UUID.fromString(picUUID)) !is Pic) {
                response.status = 404
                error("No such image")
                return
            }

            val q = request.queryString ?: ""
            if (numberRegex.matches(q)) {
                val qi = q.toInt()
                if (qi < ImageTypes.size) {
                    imgType = qi
                }
            }

            val imgTypeConvertor = ImageTypes[imgType].first
            // TODO: Thumbs for images?
            request.setAttribute("imgSrc", ServletUtils.getPathForHTML(request, "/Image/$imgTypeConvertor/$picUUID"))
            request.setAttribute("imgURL", ServletUtils.getPathForHTML(request, "/Image/$imgTypeConvertor/$picUUID"))

            if (imgType - 1 >= 0) {
                request.setAttribute("prev", ServletUtils.getPathForHTML(request, "/Image/$picUUID?${imgType - 1}"))
            }
            if (imgType + 1 < ImageTypes.size) {
                request.setAttribute("next", ServletUtils.getPathForHTML(request, "/Image/$picUUID?${imgType + 1}"))
            }

            val rd = request.getRequestDispatcher("/WEB-INF/image.jsp")
            rd.forward(request, response)
        }
    }

    private fun writeImgToResponse(pic: Pic, response: HttpServletResponse) {
        val out = response.outputStream

        response.contentType = pic.type
        response.setContentLength(pic.length)
        val inputStream = ByteArrayInputStream(pic.bytes)
        val input = BufferedInputStream(inputStream)
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
        out.println("<h1>An error has occurred.</h1>")
        out.println("<h2>$mess</h2>")
        out.close()
        return
    }

    companion object {
        private val serialVersionUID = 1L
    }
}