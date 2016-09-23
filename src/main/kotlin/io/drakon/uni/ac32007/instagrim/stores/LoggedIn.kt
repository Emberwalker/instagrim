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
