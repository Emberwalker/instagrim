package io.drakon.uni.ac32007.instagrim.lib.db

import com.datastax.driver.core.*
import org.slf4j.LoggerFactory
import java.util.*

object Cassandra {

    private var _cluster: Cluster? = null
    private var _session: Session? = null
    private val HOST = "127.0.0.1"  //at least one starting point to talk to
    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun getHosts(cl: Cluster?): Array<String>? {
        var cluster = cl
        if (cluster == null) {
            log.info("Creating cluster connection")
            cluster = Cluster.
                    builder().
                    addContactPoint(HOST).
                    build()
        }
        log.info("Cluster name {}", cluster!!.clusterName)
        val mdata: Metadata
        try {
            mdata = cluster.metadata
        } catch (et: Exception) {
            log.error("Can't get metadata", et)
            return null
        }

        val hosts = mdata!!.allHosts
        val sHosts = ArrayList<String>()

        val it = hosts.iterator()
        var i = 0
        while (it.hasNext()) {
            val ch = it.next()
            sHosts.add(ch.address.toString())

            log.debug("Hosts" + ch.address.toString())
            i++
        }

        return sHosts.toTypedArray()
    }

    private fun getCluster(): Cluster {
        var cl = _cluster
        if (cl is Cluster) return cl

        log.debug("getCluster")
        cl = Cluster.builder().addContactPoint(HOST).build()
        if (getHosts(cl) == null) {
            // TODO: Deal with missing server gracefully
            throw RuntimeException("getHosts failed for cluster")
        }
        Keyspaces.setUpKeySpaces(cl)

        _cluster = cl
        return cl
    }

    /**
     * Get the application Cassandra session.
     *
     * @return The session.
     */
    fun getSession(): Session {
        var s = _session
        if (s is Session) return s

        log.debug("getSession")
        val cl = getCluster()
        s = cl.connect("instagrim")

        _session = s
        return s
    }

    /**
     * Terminates the Cassandra session and cluster connection properly, to avoid app server issues.
     */
    fun close() {
        log.info("Closing session and cluster connection.")
        _session?.close()
        _cluster?.close()
    }

}
