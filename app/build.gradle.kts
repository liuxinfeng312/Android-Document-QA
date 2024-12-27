import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
}

val geminiKey: String = gradleLocalProperties(rootDir, providers).getProperty("geminiKey")

android {
    namespace = "com.ml.shubham0204.docqa"
    compileSdk = 34
//    sourceSets {
//        getByName("main") {
//            jniLibs.srcDirs("src/main/jniLibs")
//        }
//    }
    defaultConfig {
        applicationId = "com.ml.shubham0204.docqa"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
//        ndk {
//            // 指定支持的架构
//            abiFilters.add("arm64-v8a")
//            abiFilters.add("armeabi-v7a")
//        }
    }


    buildTypes {
        // Add the field 'geminiKey' in the build config
        // See https://stackoverflow.com/a/60474096/13546426
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField( "String" , "geminiKey" , geminiKey)
        }
        debug {
            buildConfigField( "String" , "geminiKey" , geminiKey)
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
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    applicationVariants.configureEach {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}





dependencies {
//   implementation(files("libs/wBankAi-debug.aar"))
//   implementation(files("libs/PaddlePredictor.jar"))
    implementation("com.github.equationl:paddleocr4android:v1.1.1")
//    implementation ("com.github.equationl:paddleocr4android:v1.1.1-OpenCL")
//    implementation (project(":PaddleOCR4Android"))

    implementation("com.google.code.gson:gson:2.9.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material3.icons.extended)
    implementation(libs.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // Apache POI
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)

    // Sentence Embeddings
    // https://github.com/shubham0204/Sentence-Embeddings-Android
    implementation("com.github.shubham0204:Sentence-Embeddings-Android:0.0.3")
    // iTextPDF - for parsing PDFs
    implementation("com.itextpdf:itextpdf:5.5.13.3")
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.play.services.mlkit.text.recognition)


    // ObjectBox - vector database
    debugImplementation("io.objectbox:objectbox-android-objectbrowser:4.0.0")
    releaseImplementation("io.objectbox:objectbox-android:4.0.0")

    // Gemini SDK - LLM
//    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")

    // compose-markdown
    // https://github.com/jeziellago/compose-markdown
    implementation("com.github.jeziellago:compose-markdown:0.5.0")

    // Koin dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    implementation(libs.koin.androidx.compose)
    ksp(libs.koin.ksp.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apply(plugin = "io.objectbox")