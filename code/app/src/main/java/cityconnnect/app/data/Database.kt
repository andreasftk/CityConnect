package cityconnnect.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, db_name, null, db_version) {
    companion object {
        const val db_name = "CityConnect"
        const val db_version = 1
        const val db_table = "User"
        const val user_username = "username"
        const val user_id = "id"
        const val user_email = "email"
        const val user_password = "password" // Corrected typo in column name
        const val user_name = "name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE $db_table "
                + "($user_id INTEGER PRIMARY KEY,"
                + "$user_username VARCHAR(255),"
                + "$user_email VARCHAR(255),"
                + "$user_password VARCHAR(255),"
                + "$user_name VARCHAR(255))")
        db?.execSQL(createUserTable)
        val q1 = "CREATE TABLE Citizens(id INTEGER NOT NULL PRIMARY KEY, username VARCHAR(255) NOT NULL UNIQUE,email VARCHAR(255) NOT NULL UNIQUE,password VARCHAR(25) NOT NULL, name VARCHAR(25) NOT NULL)"

        db?.execSQL(q1)
        db?.execSQL(  "INSERT INTO " + db_table + " VALUES(2,\"andreasfotakis88\",\"andreasfotakis88@gmail.com\",\"1234567\",\"Athens\")")
        db?.execSQL(  "INSERT INTO Citizens VALUES(2,\"andreasfotakis88\",\"andreasfotakis88@gmail.com\",\"1234567\",\"Athens\")")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $db_table")
        db?.execSQL("DROP TABLE IF EXISTS Citizen")
        onCreate(db)

    }
}
