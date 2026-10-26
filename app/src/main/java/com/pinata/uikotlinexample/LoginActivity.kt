package com.pinata.uikotlinexample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

/** Gets a user token the way a partner backend would: company token first, then the user's token. */
class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val session = Session(this)
    if (session.userToken != null) return openApp()

    setContentView(R.layout.activity_login)
    val clientId = findViewById<EditText>(R.id.clientId).apply { setText(BuildConfig.CLIENT_ID) }
    val clientKey = findViewById<EditText>(R.id.clientKey).apply { setText(BuildConfig.CLIENT_KEY) }
    val clientSecret = findViewById<EditText>(R.id.clientSecret).apply { setText(BuildConfig.CLIENT_SECRET) }
    val userEmail = findViewById<EditText>(R.id.userEmail).apply { setText(BuildConfig.USER_EMAIL) }
    val login = findViewById<Button>(R.id.login)

    login.setOnClickListener {
      val email = userEmail.text.toString().trim()
      val fields = listOf(clientId, clientKey, clientSecret).map { it.text.toString().trim() }
      if (email.isEmpty() || fields.any { it.isEmpty() }) {
        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }
      login.isEnabled = false
      thread {
        val result = runCatching {
          val service = PinataService()
          val companyToken = service.getCompanyToken(fields[0], fields[1], fields[2])
          service.getUserToken(companyToken, email)
        }
        runOnUiThread {
          login.isEnabled = true
          result
            .onSuccess { userToken ->
              session.login(userToken)
              openApp()
            }
            .onFailure { Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG).show() }
        }
      }
    }
  }

  private fun openApp() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }
}
