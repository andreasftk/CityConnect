package cityconnnect.app.data

import cityconnnect.app.data.model.LoggedInUser


import java.io.IOException


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

class LoginDataSource {
//    init {
//        val database = getDatabase()
//        var collection = database.getCollection<Users>("Users")
//    }
//
    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }
//
    fun logout() {
        // TODO: revoke authentication
    }
}
