package cityconnnect.app.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.SQLDataException

class DatabaseManager(private val context: Context?) {
    private var dbHelper: Database? = null
    private var db: SQLiteDatabase? = null

    @Throws(SQLDataException::class)
    fun open(): DatabaseManager {
        dbHelper = context?.let { Database(it) }
        db = dbHelper!!.getWritableDatabase()
        return this
    }

    fun close() {
        dbHelper?.close()
    }

    fun fetchU(): Cursor? {
        val columns = arrayOf<String>(
            Database.user_id,
            Database.user_username,
            Database.user_email,
            Database.user_password,
            Database.user_name,
        )
        val cursor = db!!.query(Database.db_table, columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }
    fun fetchCitizens(): Cursor? {
        val columns =
            arrayOf("id", "username","email", "password", "name")
        val cursor = db!!.query("Citizens", columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }


}