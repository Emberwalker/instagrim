package io.drakon.uni.ac32007.instagrim.lib

import com.datastax.driver.core.*
import org.slf4j.LoggerFactory
import java.util.*

object CassandraHosts {

    private lateinit var _cluster: Cluster
    internal var Host = "127.0.0.1"  //at least one starting point to talk to
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun getHosts(cluster: Cluster?): Array<String>? {
        var cluster = cluster
        if (cluster == null) {
            log.info("Creating cluster connection")
            cluster = Cluster.builder().addContactPoint(Host).build()
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

    fun getCluster(): Cluster {
        log.debug("getCluster")
        _cluster = Cluster.builder().addContactPoint(Host).build()
        if (getHosts(_cluster) == null) {
            // TODO: Deal with missing server gracefully
            throw RuntimeException("getHosts failed for cluster")
        }
        Keyspaces.SetUpKeySpaces(_cluster)

        return _cluster
    }

    fun close() {
        log.info("Closing cluster connection.")
        _cluster.close()
    }

}
