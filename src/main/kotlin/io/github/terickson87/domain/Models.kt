package io.github.terickson87.domain

import io.github.terickson87.plugins.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class NoteRequest(val body: String)

@Serializable(InstantSerializer::class)
data class NoteResponse(val id: Int, val createdAt: Instant, val body: String)

@Serializable
data class ErrorResponse(val message: String)