import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.9.20"
    id("com.github.ben-manes.versions") version "0.50.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "org.alexn"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rometools:rome:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.slf4j:slf4j-nop:2.0.9")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
    checkForGradleUpdate = true
    outputFormatter = "html"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

// https://www.baeldung.com/kotlin/gradle-executable-jar
tasks.register<Jar>("fatJar") {
    // We need this for Gradle optimization to work
    dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
    archiveClassifier.set("standalone") // Naming the jar
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // Provided we set it up in the application plugin configuration
    manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } +
        sourcesMain.output
    from(contents)
}

tasks.assemble {
    dependsOn("fatJar")
}
