import java.util.Properties
import java.io.FileInputStream

// local.properties 파일에서 GNEWS_API_KEY 읽기
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Room DB 등 Annotation 기반 라이브러리용
}

android {
    namespace = "com.example.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig를 통해 API 키를 코드 내에서 안전하게 사용 가능
        val gnewsApiKey = localProperties.getProperty("GNEWS_API_KEY") ?: ""
        buildConfigField("String", "GNEWS_API_KEY", "\"$gnewsApiKey\"")

        val nytApiKey = localProperties.getProperty("NYT_API_KEY") ?: ""
        buildConfigField("String", "NYT_API_KEY", "\"$nytApiKey\"")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true        // Jetpack Compose 활성화
        buildConfig = true    // BuildConfig.* 접근 허용 (API 키 포함)
    }
}

dependencies {
    // 기본 Android 및 Compose 구성 요소
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose Material2 & Material3
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.compose.material3:material3:1.2.1")

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 네트워크 (Retrofit + Gson + Logging)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Accompanist: SwipeRefresh & FlowLayout
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.28.0")

    // Room DB
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
