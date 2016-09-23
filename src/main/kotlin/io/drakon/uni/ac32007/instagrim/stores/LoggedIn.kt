/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.drakon.uni.ac32007.instagrim.stores

class LoggedIn {
    internal var logedin = false
    var username: String? = null
    fun LogedIn() {

    }

    fun setLogedin() {
        logedin = true
    }

    fun setLogedout() {
        logedin = false
    }

    fun setLoginState(logedin: Boolean) {
        this.logedin = logedin
    }

    fun getlogedin(): Boolean {
        return logedin
    }
}
