import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "br.edu.ifce"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.apache.activemq:activemq-all:5.16.3")
    implementation("javax.jms:javax.jms-api:2.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation(fileTree(mapOf("dir" to "river/lib", "include" to "*.jar")))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ChatMonitor"
            packageVersion = "1.0.0"
        }
    }
}