package io.github.terickson87.adapter

import io.github.terickson87.domain.NoteRequest
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class NotesService(private val database: Database) {

    object NotesTable : IntIdTable() {
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")
        val body = text("body")
    }

    class DbNote(id: EntityID<Int>): IntEntity(id){
        companion object: IntEntityClass<DbNote>(NotesTable)
        var createdAt by NotesTable.createdAt
        var updatedAt by NotesTable.updatedAt
        var body by NotesTable.body
    }

    suspend fun createNote(noteRequest: NoteRequest): DbNote =
        transaction(database) {
            DbNote.new {
                body = noteRequest.body
                createdAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
                updatedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            }
        }


    suspend fun getAllNotes(): List<DbNote> =
        transaction(database) {
            DbNote.all().toList()
        }

    suspend fun getNoteById(id: Int): DbNote? =
        transaction(database) {
            DbNote.findById(id)
        }

    suspend fun updateNoteById(id: Int, noteRequest: NoteRequest): DbNote? =
        getNoteById(id)?.let {
            transaction(database) {
                it.body = noteRequest.body
                it.updatedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            }
        }?.let {
            getNoteById(id)
        }

    suspend fun deleteNoteById(id: Int): Boolean =
        transaction(database) {
            DbNote.findById(id)?.delete()?.let { true } ?: false
        }
}