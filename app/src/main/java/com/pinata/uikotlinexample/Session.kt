package com.pinata.uikotlinexample

import android.content.Context

/** The logged-in user's token, kept across launches (the React Native example uses AsyncStorage). */
class Session(context: Context) {

  private val prefs = context.getSharedPreferences("logged-in-user", Context.MODE_PRIVATE)

  val userToken: String? get() = prefs.getString("token", null)

  fun login(userToken: String) = prefs.edit().putString("token", userToken).apply()

  fun logout() = prefs.edit().clear().apply()
}
