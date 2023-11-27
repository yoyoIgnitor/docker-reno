import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import java.util.UUID

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.palantir.docker-run") version "0.34.0"
    id("com.google.protobuf") version "0.8.19"

    alias(kotlinPlugins.plugins.jvm)
    alias(libs.plugins.flyway)
    alias(kotlinPlugins.plugins.spring)
    alias(kotlinPlugins.plugins.jooq)
    alias(detektCatalog.plugins.detekt)
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks.bootJar {
    archiveBaseName.set("kiran-app-api")
    mainClass.set("com.gk.kiran.api.KiranApiServerApplicationKt")
}

var protobufJavaVersion = "3.21.6"
var grpcVersion = "1.50.2"
var grpcKotlinVersion = "1.3.0"
val coroutinesVersion = "1.7.3"

detekt {
    config = rootProject.files("detekt.yml")
}

repositories {
    maven {
        url = uri("https://kiranrenovate.jfrog.io/artifactory/demo-maven-remote/")
        credentials {
            username = project.extra.get("artifactoryUsername")!!.toString()
            password = project.extra.get("artifactoryPassword")!!.toString()
        }
    }
}