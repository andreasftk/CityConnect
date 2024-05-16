package cityconnnect.app

import android.content.Context
import cityconnnect.app.data.User
import cityconnnect.app.data.ApiClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Citizen(
    id: Int,
    username: String?,
    email: String?,
    password: String?,
    name: String?,

) : User(id,username.toString(), email.toString(), password.toString(), name.toString()) {
    @JvmName("getUsernameCitizen")
    fun getUsernameCitizen(): String {
        return username
    }

    @JvmName("getIdCitizen")
    fun getIdCitizen(): Int {
        return id
    }

    @JvmName("getEmailCitizen")
    fun getEmailCitizen(): String {
        return email
    }

    @JvmName("getPasswordCitizen")
    fun getPasswordCitizen(): String {
        return password
    }

    @JvmName("getNameCitizen")
    fun getNameCitizen(): String? {
        return name
    }

    companion object {
        fun getCitizen(context: Context?, callback: (ArrayList<Citizen>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.fetchData()

            call?.enqueue(object : Callback<ArrayList<User?>?> {
                override fun onResponse(
                    call: Call<ArrayList<User?>?>,
                    response: Response<ArrayList<User?>?>
                ) {
                    if (response.isSuccessful) {
                        val citizenList = response.body()?.filterNotNull()?.map { user ->
                            Citizen(
                                user.id,
                                user.username,
                                user.email,
                                user.password,
                                user.name
                            )
                        }?.let { ArrayList(it) } ?: ArrayList()
                        callback(citizenList)
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
