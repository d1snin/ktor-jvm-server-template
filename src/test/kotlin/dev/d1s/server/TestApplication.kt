/*
 * Copyright 2022-2023 Mikhail Titov
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

package dev.d1s.server

import dev.d1s.server.route.RouteInstaller
import dev.d1s.server.testconfiguration.TestConfigurers
import dev.d1s.server.util.withEach
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.testing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.lighthousegames.logging.logging
import org.koin.dsl.module as koinModule

private val logger = logging()

fun withTestApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
    logger.i {
        "Creating test application..."
    }

    val koinModule = koinModule {}

    environment {
        applyServerConfigurations(koinModule)
    }

    application {
        applyApplicationConfigurations(koinModule)
    }

    val client = makeClient()

    installRoutes()

    block(client)
}

private fun ApplicationEngineEnvironmentBuilder.applyServerConfigurations(koinModule: Module) {
    logger.i {
        "Applying test server configurations..."
    }

    TestConfigurers.ServerConfigurers.withEach {
        configure(koinModule)
    }
}

private fun Application.applyApplicationConfigurations(koinModule: Module) {
    logger.i {
        "Applying test application configurations..."
    }

    TestConfigurers.ApplicationConfigurers.withEach {
        configure(koinModule)
    }
}

private fun ApplicationTestBuilder.makeClient() = createClient {
    install(ContentNegotiation) {
        jackson()
    }
}

private fun ApplicationTestBuilder.installRoutes() {
    object : KoinComponent {

        private val routeInstaller by inject<RouteInstaller>()

        init {
            routing {
                with(routeInstaller) {
                    installRoutes()
                }
            }
        }
    }
}