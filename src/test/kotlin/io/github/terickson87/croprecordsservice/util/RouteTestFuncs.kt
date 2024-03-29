package io.github.terickson87.croprecordsservice.util

import io.github.terickson87.croprecordsservice.adapter.accessor.NotesAccessor
import io.github.terickson87.croprecordsservice.plugins.configureRouting
import io.github.terickson87.croprecordsservice.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.mockk
import kotlinx.serialization.json.Json

class RouteTestFuncs {
    companion object {}
}

fun RouteTestFuncs.Companion.baseCall(notesAccessor: NotesAccessor? = null, block: (HttpClient) -> Unit): Unit =
    testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        application {
            configureSerialization()
            configureRouting(notesAccessor ?: mockk())
        }
        block(client)
    }

fun RouteTestFuncs.Companion.testGet(
    path: String,
    notesAccessor: NotesAccessor,
    verifyBlock: (HttpResponse) -> Unit): Unit =
    testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        application {
            configureSerialization()
            configureRouting(notesAccessor)
        }
        client.get(path).apply { verifyBlock(this) }
    }

inline fun <reified T> RouteTestFuncs.Companion.testPostJsonBody(
    path: String,
    bodyToSet: T,
    notesAccessor: NotesAccessor,
    crossinline verifyBlock: (HttpResponse) -> Unit): Unit =
    testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        application {
            configureSerialization()
            configureRouting(notesAccessor)
        }

        client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(bodyToSet)
        }
            .apply { verifyBlock(this) }
    }