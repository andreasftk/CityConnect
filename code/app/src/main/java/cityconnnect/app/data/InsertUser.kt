package cityconnnect.app.data

import cityconnnect.app.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InsertUser : AppCompatActivity() {
    private lateinit var idField: EditText
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var add: Button
    private lateinit var errorMessage: TextView
    private var myAPI: MyAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        idField = findViewById(R.id.id)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        name = findViewById(R.id.name)
        add = findViewById(R.id.add)
        errorMessage = findViewById(R.id.error_message)

        add.setOnClickListener {
            insertData()
        }
    }

    private fun insertData() {
        val id = idField.text.toString().toInt()
        val usernameText = username.text.toString()
        val emailText = email.text.toString()
        val passwordText = password.text.toString()
        val nameText = name.text.toString()

        val gson = GsonBuilder().setLenient().create() // Set Gson to be lenient
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Pass Gson instance
            .build()

        myAPI = retrofit.create(MyAPI::class.java)

        val userCall = myAPI!!.insertData(id, usernameText, emailText, passwordText, nameText)
        userCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@InsertUser,
                        "Data successfully inserted",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearFields()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage.text = errorBody
                    errorMessage.visibility = View.VISIBLE
                    Toast.makeText(
                        this@InsertUser,
                        "Failed to insert data",
                        Toast.LENGTH_SHORT

                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                errorMessage.text = "Failed to insert data: ${t.message}"
                errorMessage.visibility = View.VISIBLE
                Toast.makeText(
                    this@InsertUser,
                    "Failed to insert data: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun clearFields() {
        idField.setText("")
        username.setText("")
        email.setText("")
        password.setText("")
        name.setText("")
    }
    companion object {
        private const val BASE_URL = "https://appcityconnect.000webhostapp.com/student/"
    }
}