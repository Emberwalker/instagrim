package io.drakon.uni.ac32007.instagrim.models

import com.datastax.driver.core.BoundStatement
import io.drakon.uni.ac32007.instagrim.lib.SimpleSHA1
import io.drakon.uni.ac32007.instagrim.lib.db.Cassandra
import org.slf4j.LoggerFactory
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException

class User {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun RegisterUser(username: String, Password: String): Boolean {
        val EncodedPassword: String
        try {
            EncodedPassword = SimpleSHA1.SHA1(Password)
        } catch (et: UnsupportedEncodingException) {
            log.warn("Can't check your password", et)
            return false
        } catch (et: NoSuchAlgorithmException) {
            log.warn("Can't check your password", et)
            return false
        }

        val session = Cassandra.getSession()
        val ps = session.prepare("insert into userprofiles (login,password) Values(?,?)")

        val boundStatement = BoundStatement(ps)
        session.execute(// this is where the query is executed
                boundStatement.bind(// here you are binding the 'boundStatement'
                        username, EncodedPassword))
        //We are assuming this always works.  Also a transaction would be good here !

        return true
    }

    fun IsValidUser(username: String, Password: String): Boolean {
        val EncodedPassword: String
        try {
            EncodedPassword = SimpleSHA1.SHA1(Password)
        } catch (et: UnsupportedEncodingException) {
            log.warn("Can't check your password", et)
            return false
        } catch (et: NoSuchAlgorithmException) {
            log.warn("Can't check your password", et)
            return false
        }

        val session = Cassandra.getSession()
        val ps = session.prepare("select password from userprofiles where login =?")
        val boundStatement = BoundStatement(ps)
        val rs = session.execute(// this is where the query is executed
                boundStatement.bind(// here you are binding the 'boundStatement'
                        username))
        if (rs.isExhausted) {
            log.debug("No Images returned")
            return false
        } else {
            for (row in rs) {
                val StoredPass = row.getString("password")
                if (StoredPass.compareTo(EncodedPassword) == 0)
                    return true
            }
        }


        return false
    }

}
