plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("java-library")
}

val gdxVersion = "1.12.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    api("com.badlogicgames.gdx:gdx:1.12.1")
}
