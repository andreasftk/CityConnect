package cityconnnect.app.data

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val name: String?
) {
    @JvmName("getUsernameUser")
    fun getUsernameUser(): String {
        return username
    }

    @JvmName("getIdUser")
    fun getIdUser(): Int {
        return id
    }

    @JvmName("getEmailUser")
    fun getEmailUser(): String {
        return email
    }

    @JvmName("getPasswordUser")
    fun getPasswordUser(): String {
        return password
    }

    @JvmName("getNameUser")
    fun getNameUser(): String? {
        return name
    }

    fun joinToString(): String {
        return "$id, $username, $email, $password, $name "
    }

    companion object {
        fun getUsers(context: Context?, callback: (ArrayList<User>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.fetchData()

            call?.enqueue(object : Callback<ArrayList<User?>?> {
                override fun onResponse(
                    call: Call<ArrayList<User?>?>,
                    response: Response<ArrayList<User?>?>
                ) {
                    if (response.isSuccessful) {
                        val userList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(userList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<User?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}
