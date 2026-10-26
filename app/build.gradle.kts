import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

// Login form defaults, read from local.properties (not committed), like the .env of the React Native example.
val localProperties = Properties().apply {
  rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

fun localProperty(key: String, default: String = "") =
  "\"" + (localProperties.getProperty(key) ?: default).replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
  namespace = "com.pinata.uikotlinexample"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.pinata.uikotlinexample"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"
    buildConfigField("String", "BASE_URL", localProperty("pinata.baseUrl", "https://sandbox.pinata.ai/partners/api/v1/"))
    buildConfigField("String", "CLIENT_ID", localProperty("pinata.clientId"))
    buildConfigField("String", "CLIENT_KEY", localProperty("pinata.clientKey"))
    buildConfigField("String", "CLIENT_SECRET", localProperty("pinata.clientSecret"))
    buildConfigField("String", "USER_EMAIL", localProperty("pinata.userEmail"))
  }

  buildFeatures.buildConfig = true

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

dependencies {
  implementation("ai.pinata:pinata-ui:0.1.0")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
}
