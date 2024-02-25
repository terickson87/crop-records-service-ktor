package io.github.terickson87

import io.github.terickson87.adapter.PostgresSingleton
import io.github.terickson87.adapter.SqlNotesAccessor
import io.github.terickson87.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    PostgresSingleton.init()
    configureSerialization()
    configureHTTP()
    configureMonitoring()
    configureRouting(SqlNotesAccessor(PostgresSingleton.getDatabase()))
}
