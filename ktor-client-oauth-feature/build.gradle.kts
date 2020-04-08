import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import java.io.FileInputStream
import java.util.Properties
import java.util.Date

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.jfrog.bintray")
}

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

/**
 * Publishing
 * TODO: Need to move to separate file
 **/

val artifactName = "ktor-client-oauth-feature"
val artifactGroup = "com.kuuurt"
val artifactVersion = "0.1.1"

val pomUrl = "https://github.com/kuuuurt/ktor-client-oauth-feature"
val pomScmUrl = "https://github.com/kuuuurt/ktor-client-oauth-feature.git"
val pomIssueUrl = "https://github.com/kuuuurt/ktor-client-oauth-feature/issues"
val pomDesc = "A Kotlin Multiplatform library for automatically handling OAuth refreshes with Ktor"

val githubRepo = "kuuuurt/ktor-client-oauth-feature"
val githubReadme = "README.md"

val pomLicenseName = "Apache-2.0"
val pomLicenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
val pomLicenseDist = "repo"

val pomDeveloperId = "kuuuurt"
val pomDeveloperName = "Kurt Renzo Acosta"

group = artifactGroup
version = artifactVersion

publishing {
    publications.withType<MavenPublication>().forEach {
        it.pom.withXml {
            asNode().apply {
                appendNode("description", pomDesc)
                appendNode("name", rootProject.name)
                appendNode("url", pomUrl)
                appendNode("licenses").appendNode("license").apply {
                    appendNode("name", pomLicenseName)
                    appendNode("url", pomLicenseUrl)
                    appendNode("distribution", pomLicenseDist)
                }
                appendNode("developers").appendNode("developer").apply {
                    appendNode("id", pomDeveloperId)
                    appendNode("name", pomDeveloperName)
                }
                appendNode("scm").apply {
                    appendNode("url", pomScmUrl)
                }
            }
        }
    }
}

bintray {
    val bintrayPropertiesFile = project.rootProject.file("bintray.properties")
    val bintrayProperties = Properties()

    bintrayProperties.load(FileInputStream(bintrayPropertiesFile))
    user = bintrayProperties.getProperty("bintray.user")
    key = bintrayProperties.getProperty("bintray.key")
    publish = true
    override = true

    pkg.apply {
        repo = "libraries"
        name = artifactName
        websiteUrl = pomUrl
        githubRepo = "kuuuurt/ktor-client-oauth-feature"
        vcsUrl = pomScmUrl
        description = ""
        setLabels("kotlin", "multiplatform", "android", "ios", "ktor")
        setLicenses("Apache-2.0")
        desc = description
        issueTrackerUrl = pomIssueUrl

        version.apply {
            name = artifactVersion
            vcsTag = artifactVersion
            released = Date().toString()
        }
    }
}

tasks.named<BintrayUploadTask>("bintrayUpload") {
    dependsOn("publishToMavenLocal")
    doFirst {
        project.publishing.publications.withType<MavenPublication>().all {
            val moduleFile = buildDir.resolve("publications/$name/module.json")
            if (moduleFile.exists()) {
                artifact(object : org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact(moduleFile) {
                    override fun getDefaultExtension() = "module"
                })
            }
        }
    }
}

afterEvaluate {
    project.publishing.publications.withType<MavenPublication>().all {
        groupId = artifactGroup

        artifactId = if (name.contains("metadata")) {
            "$artifactName-common"
        } else if (name.contains("kotlinMultiplatform")) {
            artifactName
        } else {
            "$artifactName-$name"
        }
    }
    bintray {
        setPublications(*publishing.publications
            .map { it.name }
            .toTypedArray())
    }
}