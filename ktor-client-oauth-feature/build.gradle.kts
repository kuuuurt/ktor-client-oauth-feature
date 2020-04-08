plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.jfrog.bintray")
}

apply(from = "publish.gradle.kts")

kotlin {
    jvm()
    ios()
    sourceSets {
        val ktorVersion = "1.3.2"
        val coroutinesVersion = "1.3.5"
        val commonMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${coroutinesVersion}")
                implementation("io.ktor:ktor-client-core:${ktorVersion}")
            }
        }
        val jvmMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
                implementation("io.ktor:ktor-client-core-jvm:${ktorVersion}")
            }
        }
        val iosMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${coroutinesVersion}")
                implementation("io.ktor:ktor-client-core-native:${ktorVersion}")
            }
        }
    }
}