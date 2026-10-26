package com.pinata.uikotlinexample

import android.util.Base64
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.json.JSONObject

/**
 * Partner API calls the example needs to log a user in. Blocking: call them off the main thread.
 * A real app makes these calls from its backend: the company credentials must never ship in an app.
 */
class PinataService(private val baseUrl: String = BuildConfig.BASE_URL) {

  fun getCompanyToken(clientId: String, clientKey: String, clientSecret: String): String {
    val payload = JSONObject().put("clientId", clientId).put("clientKey", clientKey).toString()
    return post("auth/retrieve-token/", payload, mapOf("Signature" to sign(payload, clientSecret)))
  }

  fun getUserToken(companyAccessToken: String, userEmail: String): String {
    val payload = JSONObject().put("email", userEmail).toString()
    return post("auth/retrieve-user-token/", payload, mapOf("Authorization" to "Token $companyAccessToken"))
  }

  private fun sign(payload: String, secret: String): String {
    val mac = Mac.getInstance("HmacSHA256").apply { init(SecretKeySpec(secret.toByteArray(), "HmacSHA256")) }
    return Base64.encodeToString(mac.doFinal(payload.toByteArray()), Base64.NO_WRAP)
  }

  /** Posts [body] and returns `data.accessToken` from the response. */
  private fun post(path: String, body: String, headers: Map<String, String>): String {
    val connection = URL(baseUrl + path).openConnection() as HttpURLConnection
    try {
      connection.requestMethod = "POST"
      connection.doOutput = true
      connection.setRequestProperty("Content-Type", "application/json")
      headers.forEach { (name, value) -> connection.setRequestProperty(name, value) }
      connection.outputStream.use { it.write(body.toByteArray()) }
      if (connection.responseCode !in 200..299) {
        throw IOException("$path failed with HTTP ${connection.responseCode}")
      }
      val response = connection.inputStream.bufferedReader().use { it.readText() }
      return JSONObject(response).getJSONObject("data").getString("accessToken")
    } finally {
      connection.disconnect()
    }
  }
}
