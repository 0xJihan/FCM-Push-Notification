plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.10-1.0.29"
    kotlin("plugin.serialization") version "2.1.0"


}

android {
    namespace = "com.jihan.app"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.jihan.app"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.composethemer.core)
    implementation(libs.composethemer.themes)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    //* coil image
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //* networking
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)


    //*koin
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.1.0-Beta1"))
//    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    //* google fonts
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.core.splashscreen)
    //* Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)

    //* debug
    debugImplementation(libs.leakcanary.android)

    //* Room
    implementation(libs.room.ktx)
    implementation(libs.androidx.room.runtime)

    ksp(libs.androidx.room.compiler)

    //Datastore
    implementation(libs.core)
    implementation(libs.storage.datastore)
    implementation(libs.encryption.aes)


     //!Icons


    implementation(libs.icons.lucide)
    implementation(libs.computils)
    implementation(libs.lucide.icons)

 

}



