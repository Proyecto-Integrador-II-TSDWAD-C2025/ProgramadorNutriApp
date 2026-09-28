plugins {
    id("com.android.application")
}

// Para generar el APK final: -PNUTRIAPP_RELEASE_API_BASE_URL=https://dominio/api/
val releaseApiBaseUrl = providers.gradleProperty("NUTRIAPP_RELEASE_API_BASE_URL")
    .getOrElse("https://example.invalid/api/")

require(releaseApiBaseUrl.startsWith("https://") && releaseApiBaseUrl.endsWith("/")) {
    "NUTRIAPP_RELEASE_API_BASE_URL debe usar HTTPS y terminar con /."
}

fun buildConfigString(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

android {
    namespace = "com.example.nutriappmovil"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.nutriappmovil"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["usesCleartextTraffic"] = "false"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"http://10.0.2.2:8000/api/\""
            )
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "API_BASE_URL",
                buildConfigString(releaseApiBaseUrl)
            )
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
