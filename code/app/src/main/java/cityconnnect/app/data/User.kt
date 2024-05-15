package cityconnnect.app.data

import android.content.Context
import android.database.Cursor
import java.sql.SQLDataException


open class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val name: String?,


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


        fun getUsers(c: Context?): ArrayList<User> {
            try {
                val dbm: DatabaseManager = DatabaseManager(c)
                dbm.open()
                val cursor: Cursor? = dbm.fetchU()
                val userlist = ArrayList<User>()

                if (cursor != null) {
                    val value = if (cursor.moveToFirst()) {
                        do {
                            userlist.add(
                                User(
                                    cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4)
                                )
                            )
                        } while (cursor.moveToNext())
                    } else {

                    }
                    cursor.close()
                }

                
                dbm.close()
                return userlist
            } catch (e: SQLDataException) {
                throw RuntimeException(e)
            }
        }



    }
}

