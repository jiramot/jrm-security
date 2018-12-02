package im.jrm.security.util

import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class ResettableStreamHttpServletRequest(private val request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private var rawData: ByteArray? = null
    private var servletStream: ResettableServletInputStream

    init {
        this.servletStream = ResettableServletInputStream()
    }

    fun resetInputStream() {
        servletStream.stream = ByteArrayInputStream(rawData)
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(request.reader, Charset.defaultCharset())
            servletStream.stream = ByteArrayInputStream(rawData)
        }
        return servletStream
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(request.reader, Charset.defaultCharset())
            servletStream.stream = ByteArrayInputStream(rawData)
        }
        return BufferedReader(InputStreamReader(servletStream))
    }

    private inner class ResettableServletInputStream : ServletInputStream() {

        var stream: InputStream? = null

        @Throws(IOException::class)
        override fun read(): Int {
            return stream!!.read()
        }

        override fun isFinished(): Boolean {
            return false
        }

        override fun isReady(): Boolean {
            return false
        }

        override fun setReadListener(listener: ReadListener) {
        }
    }
}