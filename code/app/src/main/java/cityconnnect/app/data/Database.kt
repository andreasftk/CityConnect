package cityconnnect.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, db_name, null, db_version) {
    companion object {
        const val db_name = "CityConnect"
        const val db_version = 1
        const val db_table = "User"
        const val user_id = "id"
        const val user_email = "email"
        const val user_password = "password" // Corrected typo in column name
        const val user_name = "name"
        const val user_balance = "balance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE $db_table "
                + "($user_id INTEGER PRIMARY KEY,"
                + "$user_email TEXT,"
                + "$user_password TEXT,"
                + "$user_name TEXT,"
                + "$user_balance REAL)")
        db?.execSQL(createUserTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $db_table")
        onCreate(db)
    }
}
