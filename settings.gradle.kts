pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    exclusiveContent {
      forRepository { maven { url = uri("https://static.pinata.ai/static/android/maven") } }
      filter { includeGroup("ai.pinata") }
    }
  }
}

rootProject.name = "pinata-ui-kotlin-example-app"
include(":app")
