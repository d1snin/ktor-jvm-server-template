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

import dev.d1s.server.configuration.Configurers
import dev.d1s.server.route.RouteInstaller
import dev.d1s.server.util.makeHoconApplicationConfig
import dev.d1s.server.util.withEach
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.lighthousegames.logging.logging
import org.koin.dsl.module as koinModule

class ServerApplication : KoinComponent {

    private val routeInstaller by inject<RouteInstaller>()

    private val logger = logging()

    fun launch() {
        logger.i {
            "Starting the server..."
        }

        val applicationEngineEnvironment = applicationEngineEnvironment {
            applyConfigurations()
        }

        embeddedServer(Netty, applicationEngineEnvironment).start(wait = true)
    }

    private fun ApplicationEngineEnvironmentBuilder.applyConfigurations() {
        logger.i {
            "Applying configurations..."
        }

        module {
            val koinModule = koinModule {}

            val config = loadConfig()

            applyServerConfigurations(koinModule, config)
            applyApplicationConfigurations(koinModule, config)

            installRoutes()
        }
    }

    private fun ApplicationEngineEnvironmentBuilder.loadConfig(): ApplicationConfig {
        val hoconConfig = makeHoconApplicationConfig()

        config = hoconConfig

        return hoconConfig
    }

    private fun ApplicationEngineEnvironmentBuilder.applyServerConfigurations(
        koinModule: Module,
        config: ApplicationConfig
    ) {
        logger.d {
            "Applying server configurations..."
        }

        Configurers.ServerConfigurers.withEach {
            configure(koinModule, config)
        }
    }

    private fun Application.applyApplicationConfigurations(koinModule: Module, config: ApplicationConfig) {
        logger.d {
            "Applying application configurations..."
        }

        Configurers.ApplicationConfigurers.withEach {
            configure(koinModule, config)
        }
    }

    private fun Application.installRoutes() {
        routing {
            with(routeInstaller) {
                installRoutes()
            }
        }
    }
}