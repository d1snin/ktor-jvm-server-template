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

import com.typesafe.config.ConfigFactory
import dev.d1s.server.configuration.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.lighthousegames.logging.logging
import org.koin.dsl.module as koinModule

class ServerApplication {

    private val logger = logging()

    private val configurations = listOf(
        Config,
        ContentNegotiation,
        Database,
        Di,
        Events,
        Routing,
        Security,
        StatusPages
    )

    fun launch() {
        logger.i {
            "Starting the server..."
        }

        val applicationEngineEnvironment = applicationEngineEnvironment {
            config()
            module()
            connector()
        }

        embeddedServer(Netty, applicationEngineEnvironment).start(wait = true)
    }

    private fun ApplicationEngineEnvironmentBuilder.config() {
        val loadedHoconConfig = ConfigFactory.load()

        config = HoconApplicationConfig(loadedHoconConfig)
    }

    private fun ApplicationEngineEnvironmentBuilder.module() {
        module {
            val koinModule = koinModule {}

            configurations.forEach {
                with(it) {
                    configure(koinModule)
                }
            }
        }
    }

    private fun ApplicationEngineEnvironmentBuilder.connector() {
        connector {
            port = config.port
        }
    }
}