plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val VERSION_NAME = "0.8.2"
val VERSION_CODE = 82

android {

    compileSdk = 30
    buildToolsVersion = "30.0.3"
    flavorDimensions("version")

    defaultConfig {
        applicationId = "cn.wthee.pcrtool"
        minSdk = 21
        targetSdk = 30
        versionCode = VERSION_CODE
        versionName = VERSION_NAME


        buildConfigField("int", "SQLITE_VERSION", "80")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    productFlavors {
        create("official") {
            applicationId = "cn.wthee.pcrtool"
            dimension = "version"
            manifestPlaceholders["icon"] = "@mipmap/ic_launcher"
            resValue("string", "app_name", "PCR Tool")
        }

        create("beta") {
            applicationId = "cn.wthee.pcrtoolbeta"
            dimension = "version"
            manifestPlaceholders["icon"] = "@drawable/ic_star"
            resValue("string", "app_name", "PCR Tool βeta")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
        kotlinCompilerVersion = "1.4.32"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0-alpha06")
    implementation("androidx.preference:preference-ktx:1.1.1")

    //CircleProgressBar
    implementation("com.github.wthee:CircleProgressBar:1.0.3")

    //coil
    val coil_version = "1.2.0"
    implementation("io.coil-kt:coil:$coil_version")
    implementation("io.coil-kt:coil-gif:$coil_version")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha08")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hilt_version"]}")

    //Lifecycle
    val lifecycle_version = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    //Material
    val material_version = "1.3.0"
    implementation("com.google.android.material:material:$material_version")


    //Navigation
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha10")

    //Palette
    val palette_version = "1.0.0"
    implementation("androidx.palette:palette-ktx:$palette_version")

    //PermissionX
    val permissionx_version = "1.4.0"
    implementation("com.permissionx.guolindev:permissionx:$permissionx_version")

    //Paging3
    implementation("androidx.paging:paging-runtime-ktx:3.0.0-beta03")

    //Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit_version")

    //Room
    val room_version = "2.3.0-rc01"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //startup
    implementation("androidx.startup:startup-runtime:1.0.0")

    //umeng
    implementation("com.umeng.umsdk:common:9.3.8")
    implementation("com.umeng.umsdk:asms:1.2.2")
    implementation("com.umeng.umsdk:apm:1.2.0")

    //Viewpager2
    val viewpager2_version = "1.0.0"
    implementation("androidx.viewpager2:viewpager2:$viewpager2_version")

    //Work
    val work_version = "2.7.0-alpha02"
    implementation("androidx.work:work-runtime-ktx:$work_version")


    implementation(files("libs\\commons-compress-1.19.jar"))
    implementation(files("libs\\dec-0.1.2.jar"))
    implementation(project(":Material-Calendar-View"))

}