package io.drakon.uni.ac32007.instagrim.lib

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.SimpleStatement
import org.slf4j.LoggerFactory

object Keyspaces {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun SetUpKeySpaces(c: Cluster) {
        try {
            //Add some keyspaces here
            val createkeyspace = "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}"
            val CreatePicTable = "CREATE TABLE if not exists instagrim.Pics (" +
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
                    ")"

            val Createuserpiclist = "CREATE TABLE if not exists instagrim.userpiclist (\n" +
                    "picid uuid,\n" +
                    "user varchar,\n" +
                    "pic_added timestamp,\n" +
                    "PRIMARY KEY (user,pic_added)\n" +
                    ") WITH CLUSTERING ORDER BY (pic_added desc);"

            val CreateAddressType = "CREATE TYPE if not exists instagrim.address (\n" +
                    "      street text,\n" +
                    "      city text,\n" +
                    "      zip int\n" +
                    "  );"

            val CreateUserProfile = "CREATE TABLE if not exists instagrim.userprofiles (\n" +
                    "      login text PRIMARY KEY,\n" +
                    "     password text,\n" +
                    "      first_name text,\n" +
                    "      last_name text,\n" +
                    "      email set<text>,\n" +
                    "      addresses  map<text, frozen <address>>\n" +
                    "  );"

            val session = c.connect()

            try {
                val statement = session.prepare(createkeyspace)
                val boundStatement = BoundStatement(
                        statement)
                session.execute(boundStatement)
                log.info("Created instagrim keyspace.")
            } catch (et: Exception) {
                log.error("Error creating instagrim keyspace", et)
            }

            //now add some column families
            log.debug(CreatePicTable)

            try {
                val cqlQuery = SimpleStatement(CreatePicTable)
                session.execute(cqlQuery)
            } catch (et: Exception) {
                log.error("Error creating pic table ", et)
            }

            log.debug(Createuserpiclist)

            try {
                val cqlQuery = SimpleStatement(Createuserpiclist)
                session.execute(cqlQuery)
            } catch (et: Exception) {
                log.error("Error creating user pic list table ", et)
            }

            log.debug(CreateAddressType)

            try {
                val cqlQuery = SimpleStatement(CreateAddressType)
                session.execute(cqlQuery)
            } catch (et: Exception) {
                log.error("Error creating Address type ", et)
            }

            log.debug(CreateUserProfile)

            try {
                val cqlQuery = SimpleStatement(CreateUserProfile)
                session.execute(cqlQuery)
            } catch (et: Exception) {
                log.error("Error creating Address Profile ", et)
            }

            session.close()

        } catch (et: Exception) {
            log.error("Unknown error during keyspace/table/type init", et)
        }

    }
}
