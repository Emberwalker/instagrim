package io.drakon.uni.ac32007.instagrim.models

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException

import io.drakon.uni.ac32007.instagrim.lib.AeSimpleSHA1


class User {
    // FIXME: Make this non-nullable.
    lateinit internal var cluster: Cluster

    fun RegisterUser(username: String, Password: String): Boolean {
        val EncodedPassword: String
        try {
            EncodedPassword = AeSimpleSHA1.SHA1(Password)
        } catch (et: UnsupportedEncodingException) {
            println("Can't check your password")
            return false
        } catch (et: NoSuchAlgorithmException) {
            println("Can't check your password")
            return false
        }

        val session = cluster.connect("instagrim")
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
            EncodedPassword = AeSimpleSHA1.SHA1(Password)
        } catch (et: UnsupportedEncodingException) {
            println("Can't check your password")
            return false
        } catch (et: NoSuchAlgorithmException) {
            println("Can't check your password")
            return false
        }

        val session = cluster.connect("instagrim")
        val ps = session.prepare("select password from userprofiles where login =?")
        val boundStatement = BoundStatement(ps)
        // TODO: Look at making this not-nullable or smart castable
        val rs = session.execute(// this is where the query is executed
                boundStatement.bind(// here you are binding the 'boundStatement'
                        username))
        if (rs!!.isExhausted) {
            println("No Images returned")
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

    fun setCluster(cluster: Cluster) {
        this.cluster = cluster
    }


}
