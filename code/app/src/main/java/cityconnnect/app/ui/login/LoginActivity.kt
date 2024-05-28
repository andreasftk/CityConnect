package cityconnnect.app.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cityconnnect.app.R
import cityconnnect.app.data.User
import cityconnnect.app.databinding.ActivityLoginBinding
import cityconnnect.app.Citizen
import cityconnnect.app.MainPage

@SuppressLint("StaticFieldLeak")
lateinit var username_input: EditText
@SuppressLint("StaticFieldLeak")
lateinit var password_input: EditText
@SuppressLint("StaticFieldLeak")
lateinit var login_btn: Button

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var user_id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val button = findViewById<Button>(R.id.login)

        username_input = findViewById(R.id.username)
        password_input = findViewById(R.id.password)

        // Click button, check credentials and take you to the correct page
        button.setOnClickListener {
            loading?.visibility = View.VISIBLE
            User.getUsers(this) { usersList ->
                Citizen.getCitizen(this) { citizensList ->
                    loading?.visibility = View.GONE
                    handleLogin(username, password, usersList, citizensList)
                }
            }
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // Disable login button unless both username / password is valid
            login?.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading?.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            // Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loading?.visibility = View.VISIBLE
                    User.getUsers(this@LoginActivity) { usersList ->
                        Citizen.getCitizen(this@LoginActivity) { citizensList ->
                            loading?.visibility = View.GONE
                            handleLogin(username, password, usersList, citizensList)
                        }
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun handleLogin(
        username: EditText,
        password: EditText,
        usersList: ArrayList<User>,
        citizensList: ArrayList<Citizen>
    ) {
        var found = false
        for (user in usersList) {
            if (user.getUsernameUser() == username.text.toString() &&
                user.getPasswordUser() == password.text.toString()
            ) {
                user_id = user.getIdUser()
                found = true
                break
            }
        }

        if (found) {
            for (citizen in citizensList) {
                if (citizen.getUsernameCitizen() == username.text.toString()) {
                    val intent = Intent(this, MainPage::class.java)
                    val bundle = Bundle()
                    bundle.putInt("id", user_id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    return
                }
            }
            password.error = "Citizen not found"
        } else {
            username.error = "Invalid username or password"
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // Initiate successful logged-in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
