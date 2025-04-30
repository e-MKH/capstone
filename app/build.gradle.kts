plugins {
    // ✅ Android 애플리케이션 빌드를 위한 플러그인
    alias(libs.plugins.android.application)

    // ✅ Kotlin Android 지원 플러그인
    alias(libs.plugins.kotlin.android)

    // ✅ Jetpack Compose용 Kotlin 플러그인
    alias(libs.plugins.kotlin.compose)

    // ✅ Room Database 등에서 필요한 annotation processor
    id("kotlin-kapt")
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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // ProGuard 설정
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // ✅ Java 11 사용 설정
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // ✅ Kotlin 11 버전으로 설정
    kotlinOptions {
        jvmTarget = "11"
    }

    // ✅ Jetpack Compose 활성화
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ✅ Android 기본 코어 및 생명주기 지원
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ✅ Jetpack Compose BOM (버전 관리 통합)
    implementation(platform(libs.androidx.compose.bom))

    // ✅ Compose UI 관련 구성 요소
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // ✅ Material 2 (Compose 기본 컴포넌트)
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // ✅ Material 3 최신 디자인 컴포넌트
    implementation("androidx.compose.material3:material3:1.2.1")

    // ✅ Jetpack Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ✅ Retrofit2 - REST API 통신
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 자동 파싱

    // ✅ SwipeRefresh 기능 (뉴스 새로고침)
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // ✅ Room Database (단어장 저장 기능)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.navigation.compose)
    kapt("androidx.room:room-compiler:2.6.1") // annotation processor
    implementation("androidx.room:room-ktx:2.6.1") // 코루틴, Flow 지원

    // ✅ 테스트 관련 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Compose UI 테스트
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

