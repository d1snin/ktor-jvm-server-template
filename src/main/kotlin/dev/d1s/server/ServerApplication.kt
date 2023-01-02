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

package dev.d1s.server

import dev.d1s.server.configuration.*
import dev.d1s.server.configuration.Connector.configure
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.module.Module
import org.lighthousegames.logging.logging
import org.koin.dsl.module as koinModule

class ServerApplication {

    private val logger = logging()

    private val configurers = listOf(
        ConfigSource,
        Connector,

        Config,
        ContentNegotiation,
        Database,
        Di,
        Events,
        Routing,
        Security,
        StatusPages
    )

    private val serverConfigurers = configurers.filterIsInstance<ServerConfigurer>()
    private val applicationConfigurers = configurers.filterIsInstance<ApplicationConfigurer>()

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
        module {
            val koinModule = koinModule {}

            applyServerConfigurations(koinModule)
            applyApplicationConfigurations(koinModule)
        }
    }

    private fun ApplicationEngineEnvironmentBuilder.applyServerConfigurations(koinModule: Module) {
        serverConfigurers.withEach {
            configure(koinModule)
        }
    }

    private fun ApplicationEngineEnvironmentBuilder.applyApplicationConfigurations(koinModule: Module) {
        applicationConfigurers.withEach {
            configure(koinModule)
        }
    }

    private inline fun <T> List<T>.withEach(block: T.() -> Unit) = this.forEach {
        with(it) {
            block()
        }
    }
}