import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import java.io.FileInputStream
import java.util.*

val artifactName = "ktor-client-oauth-feature"
val artifactGroup = "com.kuuuurt"
val artifactVersion = "0.1.0"

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