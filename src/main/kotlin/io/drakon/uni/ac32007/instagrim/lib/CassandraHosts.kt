package io.drakon.uni.ac32007.instagrim.lib

import com.datastax.driver.core.*
import java.util.*

object CassandraHosts {

    private lateinit var _cluster: Cluster
    internal var Host = "127.0.0.1"  //at least one starting point to talk to

    fun getHosts(cluster: Cluster?): Array<String>? {
        var cluster = cluster
        if (cluster == null) {
            println("Creating cluster connection")
            cluster = Cluster.builder().addContactPoint(Host).build()
        }
        println("Cluster Name " + cluster!!.clusterName)
        var mdata: Metadata? = null
        try {
            mdata = cluster.metadata
        } catch (et: Exception) {
            println("Can't get metadata")
            println("Exception " + et)
            return null
        }

        val hosts = mdata!!.allHosts
        val sHosts = ArrayList<String>()

        val it = hosts.iterator()
        var i = 0
        while (it.hasNext()) {
            val ch = it.next()
            sHosts.add(ch.address.toString())

            println("Hosts" + ch.address.toString())
            i++
        }

        return sHosts.toTypedArray()
    }

    fun getCluster(): Cluster {
        println("getCluster")
        _cluster = Cluster.builder().addContactPoint(Host).build()
        if (getHosts(_cluster) == null) {
            throw RuntimeException("getHosts failed for cluster")
        }
        Keyspaces.SetUpKeySpaces(_cluster)

        return _cluster

    }

    fun close() {
        _cluster.close()
    }

}
