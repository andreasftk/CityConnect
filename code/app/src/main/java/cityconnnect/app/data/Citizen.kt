package cityconnnect.app

import android.content.Context
import android.database.Cursor
import cityconnnect.app.data.DatabaseManager
import cityconnnect.app.data.User
import java.sql.SQLDataException

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

        fun getCitizen(c: Context?): ArrayList<Citizen> {
            try {
                val dbm = DatabaseManager(c!!)
                dbm.open()
                val cursor: Cursor? = dbm.fetchCitizens()
                val cl = ArrayList<Citizen>()

                if (cursor != null) {
                    if (cursor.moveToFirst() == true) {
                        do {
                            cl.add(
                                Citizen(
                                    // First column (0-based) - username
                                    cursor.getInt(0),
                                    cursor.getString(1),  // Second column - email
                                    cursor.getString(2),  // Third column - password
                                    cursor.getString(3),
                                    cursor.getString(4),   // Fourth column - name
                                         // Fifth column - id
                                )
                            )

                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                }

                dbm.close()
                return cl
            } catch (e: SQLDataException) {
                throw RuntimeException(e)
            }
        }
    }
}
