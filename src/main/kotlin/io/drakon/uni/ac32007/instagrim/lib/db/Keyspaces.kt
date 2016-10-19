package io.drakon.uni.ac32007.instagrim.lib.db

import com.datastax.driver.core.Cluster
import org.slf4j.LoggerFactory

object Keyspaces {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val createTableStatements = mapOf(
            "Pics" to "CREATE TABLE if not exists instagrim.Pics (\n" +
                    "  user varchar,\n" +
                    "  picid uuid PRIMARY KEY,\n" +
                    "  interaction_time timestamp,\n" +
                    "  title varchar,\n" +
                    "  image blob,\n" +
                    "  imagelength int,\n" +
                    "  thumb blob,\n" +
                    "  thumblength int,\n" +
                    "  processed blob,\n" +
                    "  processedlength int,\n" +
                    "  inverted blob,\n" +
                    "  invertedlength int,\n" +
                    "  type  varchar,\n" +
                    "  name  varchar\n" +
                    ");",
            "userpiclist" to "CREATE TABLE if not exists instagrim.userpiclist (\n" +
                    "  picid uuid,\n" +
                    "  user varchar,\n" +
                    "  pic_added timestamp,\n" +
                    "  PRIMARY KEY (user,pic_added)\n" +
                    ") WITH CLUSTERING ORDER BY (pic_added desc);",
            "userprofiles" to "CREATE TABLE if not exists instagrim.userprofiles (\n" +
                    "  login text PRIMARY KEY,\n" +
                    "  password text,\n" +
                    "  first_name text,\n" +
                    "  last_name text,\n" +
                    "  email set<text>\n" +
                    ");",
            "comments" to "CREATE TABLE IF NOT EXISTS instagrim.comments (\n" +
                    "  picid uuid,\n" +
                    "  user text,\n" +
                    "  time timestamp,\n" +
                    "  comment text,\n" +
                    "  PRIMARY KEY (picid,time)\n" +
                    ") WITH CLUSTERING ORDER BY (time desc);"
    )

    // Make these seperate so they can be done ordered first, while there's no order guarentee on the above
    private val createKeyspaceStatements = arrayOf(
            "CREATE KEYSPACE IF NOT EXISTS instagrim WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}"
    )

    internal fun setUpKeySpaces(c: Cluster) {
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
