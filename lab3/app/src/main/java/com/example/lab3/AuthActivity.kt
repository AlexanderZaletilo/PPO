package com.example.lab3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private var isAuth = true

    private var register_title_string: String? = null
    private var auth_title_string: String? = null
    private var register_button_string: String? = null
    private var login_string: String? = null

    private lateinit var switchButton: Button
    private lateinit var connectButton: Button
    private lateinit var repeatTextView: TextView
    private lateinit var repeatEditText: EditText
    private lateinit var titleTextView: TextView
    private lateinit var loginEditText: EditText
    private lateinit var passEditText: EditText
    private lateinit var errorsTextView: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        register_title_string = resources.getString(R.string.registration)
        auth_title_string = resources.getString(R.string.authentication)
        login_string = resources.getString(R.string.auth)
        register_button_string = resources.getString(R.string.register)

        switchButton = findViewById(R.id.auth_button_switch)
        connectButton = findViewById(R.id.auth_button_enter)
        repeatTextView = findViewById(R.id.auth_textview_repeat_pass)
        repeatEditText = findViewById(R.id.auth_edittext_repeat_pass)
        titleTextView = findViewById(R.id.auth_title)
        loginEditText = findViewById(R.id.auth_edittext_login)
        passEditText = findViewById(R.id.auth_edittext_pass)
        errorsTextView = findViewById(R.id.auth_errors)
        // setProgressBar(binding.progressBar)
        switchButton.setOnClickListener { onSwitchClicked() }
        connectButton.setOnClickListener{ onConnectClicked() }
        auth = Firebase.auth
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private fun onSwitchClicked()
    {
        if(isAuth)
        {
            titleTextView.text = register_title_string
            repeatEditText.visibility = EditText.VISIBLE
            repeatTextView.visibility = TextView.VISIBLE
            switchButton.text = login_string
        }
        else
        {
            titleTextView.text = auth_title_string
            repeatTextView.visibility = TextView.GONE
            repeatEditText.visibility = EditText.GONE
            switchButton.text = register_button_string
        }
        isAuth = !isAuth
    }
    private fun validateForm(): Boolean {
        var valid = true
        val login = loginEditText.text.toString()
        if (login == "") {
            errorsTextView.text = "Login required."
            return false
        }
        val pass = passEditText.text.toString()
        if(pass == "") {
            errorsTextView.text = "Password required."
            return false
        }
        if(!isAuth) {
            val repeat = repeatEditText.text.toString()
            if(repeat != pass)
            {
                errorsTextView.text = "Passwords do not match."
                return false
            }
        }
        return true
    }

    private fun onConnectClicked() {
        if(!validateForm())
            return
        if(isAuth)
            signIn(loginEditText.text.toString(), passEditText.text.toString())
        else
            createAccount(loginEditText.text.toString(), passEditText.text.toString())
    }

    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    //finish()
                }
                else
                    errorsTextView.text = "Failure during registration"
            }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    errorsTextView.text = "Failure during Sign in"
                }
            }
    }
  /*

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable button
        binding.verifyEmailButton.isEnabled = false

        // Send verification email
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // [START_EXCLUDE]
                // Re-enable button
                binding.verifyEmailButton.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(baseContext,
                        "Verification email sent to ${user.email} ",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(baseContext,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
                // [END_EXCLUDE]
            }
        // [END send_email_verification]
    }

    private fun reload() {
        auth.currentUser!!.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
                Toast.makeText(this@EmailPasswordActivity,
                    "Reload successful!",
                    Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "reload", task.exception)
                Toast.makeText(this@EmailPasswordActivity,
                    "Failed to reload user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressBar()
        if (user != null) {
            binding.status.text = getString(R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified)
            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)

            binding.emailPasswordButtons.visibility = View.GONE
            binding.emailPasswordFields.visibility = View.GONE
            binding.signedInButtons.visibility = View.VISIBLE

            if (user.isEmailVerified) {
                binding.verifyEmailButton.visibility = View.GONE
            } else {
                binding.verifyEmailButton.visibility = View.VISIBLE
            }
        } else {
            binding.status.setText(R.string.signed_out)
            binding.detail.text = null

            binding.emailPasswordButtons.visibility = View.VISIBLE
            binding.emailPasswordFields.visibility = View.VISIBLE
            binding.signedInButtons.visibility = View.GONE
        }
    }

    private fun checkForMultiFactorFailure(e: Exception) {
        // Multi-factor authentication with SMS is currently only available for
        // Google Cloud Identity Platform projects. For more information:
        // https://cloud.google.com/identity-platform/docs/android/mfa
        if (e is FirebaseAuthMultiFactorException) {
            Log.w(TAG, "multiFactorFailure", e)
            val intent = Intent()
            val resolver = e.resolver
            intent.putExtra("EXTRA_MFA_RESOLVER", resolver)
            setResult(MultiFactorActivity.RESULT_NEEDS_MFA_SIGN_IN, intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_MULTI_FACTOR = 9005
    }*/
}