/*
 * Copyright 2022 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("kapt")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlinx.kover")
}

val projectGroup: String by project
val projectVersion: String by project

group = projectGroup
version = projectVersion

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    val ktorVersion: String by project
    val ktorStaticAuthVersion: String by project
    val ktorServerLiquibaseVersion: String by project
    val ktorWsEventsVersion: String by project

    val teabagsVersion: String by project

    val logbackVersion: String by project
    val kmLogVersion: String by project

    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val ktormVersion: String by project

    val koinVersion: String by project

    val dispatchVersion: String by project

    val kotlinVersion: String by project

    val mockkVersion: String by project

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("dev.d1s:ktor-static-authentication:$ktorStaticAuthVersion")
    implementation("dev.d1s:ktor-server-liquibase:$ktorServerLiquibaseVersion")
    implementation("dev.d1s:ktor-ws-events:$ktorWsEventsVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    implementation("dev.d1s.teabags:teabag-ktor-server:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-dto:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-postgres:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-ktorm:$teabagsVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.ktorm:ktorm-core:$ktormVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("com.rickbusarow.dispatch:dispatch-core:$dispatchVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("dev.d1s.server.MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

ktor {
    docker {
        localImageName.set(project.name)
    }
}