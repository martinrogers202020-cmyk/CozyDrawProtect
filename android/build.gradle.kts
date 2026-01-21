import org.gradle.api.file.RelativePath

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val gdxVersion = "1.12.1"

android {
    namespace = "com.cozyprotect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cozyprotect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
            keepDebugSymbols += "**/*.so"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("../core/src/main/assets")
        }
    }
}

dependencies {
    implementation(project(":core"))

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.google.android.gms:play-services-ads:23.0.0")

    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")

    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

val copyAndroidNatives by tasks.registering(Copy::class) {
    val natives = configurations.runtimeClasspath.get().filter { it.name.contains("natives") }
    from(natives.map { zipTree(it) })
    include("**/*.so")
    into(layout.projectDirectory.dir("src/main/jniLibs"))
    eachFile {
        val segments = relativePath.segments
        val libIndex = segments.indexOf("lib")
        if (libIndex != -1 && segments.size > libIndex + 2) {
            val abi = segments[libIndex + 1]
            val soName = segments.last()
            relativePath = RelativePath(true, abi, soName)
        }
    }
    includeEmptyDirs = false
}

tasks.named("preBuild") {
    dependsOn(copyAndroidNatives)
}
