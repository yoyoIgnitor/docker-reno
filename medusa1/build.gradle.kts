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
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.palantir.docker-run") version "0.34.0"
    id("com.google.protobuf") version "0.9.4"

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
dependencyManagement {
    imports {
        /**
         * Force aligned version of Coroutines (to be compatible with Kotest).
         * ad1. Coroutines are currently not anywhere else from the test*Classpath configuration
         * ad2. Version of Coroutines is set by Spring dependencyManagement (cannot be enforced by enforcePlatform)
         */
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:$coroutinesVersion")
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:3.0.2")
        mavenBom("org.zalando:logbook-bom:3.5.0")
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:4.8.3")
    }
}

val flywayMigration by configurations.creating

dependencies {
    detektPlugins(detektCatalog.formatting)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
    implementation("org.springframework.integration:spring-integration-core")
    implementation("com.google.cloud:spring-cloud-gcp-starter-storage")

    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation(libs.software.amazon.awssdk.sts)

    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    compileOnly("io.grpc:grpc-stub:$grpcVersion")

    implementation(libs.io.server)
    implementation(libs.netty.socketio)
    implementation(libs.org.json)

    implementation(libs.flyway)
    implementation(libs.commons.compress)
    implementation(libs.aws.sdk.s3)
    implementation(libs.java.jwt)
    implementation(libs.retrofit)
    implementation(libs.retrofitjacksonconverter)
    implementation(libs.retrofit.micrometer)

    implementation(libs.kotlin.logging)
    implementation(libs.s1.auditlog)

    implementation(libs.io.micrometer.micrometer.registry.statsd)
    implementation(libs.io.micrometer.micrometer.registry.prometheus)
    implementation(libs.org.aspectj.aspectjweaver)
    implementation(libs.com.github.sonus21.rqueue.spring.boot.starter)
    implementation(libs.db.scheduler.spring.boot.starter)
    implementation(libs.resilience4j.retry)
    implementation(libs.resilience4j.retrofit)

    implementation(libs.okhttplogginginterceptor)

    implementation(libs.google.oauth.client)

    implementation(project(":jquery-builder"))

    implementation("org.zalando:logbook-spring-boot-starter") {
        exclude(group = "org.zalando", module = "logbook-httpclient")
    }
    implementation("org.zalando:logbook-logstash") {
        exclude(group = "net.logstash.logback", module = "logstash-logback-encoder")
    }

    runtimeOnly(libs.postgresql)

    flywayMigration(libs.postgresql)
    jooqGenerator(libs.postgresql)
    jooqGenerator(libs.bindapi)

    testImplementation(testCatalog.bundles.test.core)
    testImplementation(testCatalog.bundles.test.api)
    testImplementation(testCatalog.mockk)

    testImplementation(testCatalog.testcontainers.testcontainers)
    testImplementation(testCatalog.testcontainers.postgresql)

    testImplementation(testCatalog.serpro69.faker)
    testImplementation(testCatalog.springmockk)
    testImplementation(testCatalog.json.unit)
    testImplementation(testCatalog.okhttp.mockwebserver)
    testImplementation(testCatalog.kotest.wiremock)
}