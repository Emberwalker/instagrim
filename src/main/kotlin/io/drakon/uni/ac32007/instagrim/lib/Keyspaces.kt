package io.drakon.uni.ac32007.instagrim.lib

import com.datastax.driver.core.Cluster
import org.slf4j.LoggerFactory

object Keyspaces {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val createTableStatements = mapOf(
            "Pics" to "CREATE TABLE if not exists instagrim.Pics (" +
                    " user varchar," +
                    " picid uuid, " +
                    " interaction_time timestamp," +
                    " title varchar," +
                    " image blob," +
                    " thumb blob," +
                    " processed blob," +
                    " imagelength int," +
                    " thumblength int," +
                    "  processedlength int," +
                    " type  varchar," +
                    " name  varchar," +
                    " PRIMARY KEY (picid)" +
                    ")",
            "userpiclist" to "CREATE TABLE if not exists instagrim.userpiclist (\n" +
                    "  picid uuid,\n" +
                    "  user varchar,\n" +
                    "  pic_added timestamp,\n" +
                    "  PRIMARY KEY (user,pic_added)\n" +
                    ") WITH CLUSTERING ORDER BY (pic_added desc);",
            "address" to "CREATE TYPE if not exists instagrim.address (\n" +
                    "  street text,\n" +
                    "  city text,\n" +
                    "  zip int\n" +
                    ");"
    )

    // Make these seperate so they can be done ordered, while there's no order guarentee on the above
    private val createKeyspaceStatements = arrayOf(
            "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}",
            "CREATE TYPE if not exists instagrim.address (\n" +
                    "  street text,\n" +
                    "  city text,\n" +
                    "  zip int\n" +
                    ");"
    )

    fun setUpKeySpaces(c: Cluster) {
        val session = c.connect()
        for (statement in createKeyspaceStatements) {
            try {
                log.debug("KEYSPACE CREATION: {}", statement)
                session.execute(statement)
            } catch (ex: Exception) {
                log.error("Error setting up keyspace: {}", ex)
            }
        }

        for ((table, statement) in createTableStatements) {
            try {
                log.debug("Creating table {}", table)
                session.execute(statement)
            } catch (ex: Exception) {
                log.error("Error creating table " + table, ex)
            }
        }
    }
}
