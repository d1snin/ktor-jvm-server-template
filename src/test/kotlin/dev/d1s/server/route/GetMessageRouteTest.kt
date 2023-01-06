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

package dev.d1s.server.route

import dev.d1s.server.dto.Message
import dev.d1s.server.withTestApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.test.Test

class GetMessageRouteTest {

    @Test
    fun `test message route`() = withTestApplication { client ->
        val response = client.get(Routes.GET_MESSAGE_ROUTE)
        val body: Message = response.body()

        expectThat(response.status) isEqualTo HttpStatusCode.OK
        expectThat(body.message) isEqualTo "Hello, world!"
    }
}