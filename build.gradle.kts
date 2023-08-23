import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

group = "com.arnyminerz"
version = "1.0.0"

allprojects {
    tasks.withType(KotlinCompile::class).all {
        kotlinOptions {
            freeCompilerArgs += "-Xcontext-receivers"
        }
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation("org.apache.maven:maven-artifact:3.9.1")
                implementation("com.akuleshov7:ktoml-core:0.4.1")
                implementation("com.akuleshov7:ktoml-file:0.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0")
                implementation("com.darkrockstudios:mpfilepicker:1.2.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "modpackcreator"
            packageVersion = "1.0.0"
        }
    }
}
