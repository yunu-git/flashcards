package nz.ac.canterbury.seng303.as1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.as1.datastore.Storage
import nz.ac.canterbury.seng303.as1.models.Note
import kotlin.random.Random

class NoteViewModel(private val noteStorage: Storage<Note>): ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList());
    val notes: StateFlow<List<Note>> get() = _notes

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote
    fun getNoteById(noteId: Int?) = viewModelScope.launch {
        if (noteId != null) {
            _selectedNote.value = noteStorage.get { it.getIdentifier() == noteId }.first()
        } else {
            _selectedNote.value = null
        }
    }

    fun getNotes() = viewModelScope.launch {
        noteStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }.collect{_notes.emit(it)}
    }

    fun loadDefaultNotesIfNoneExist() = viewModelScope.launch {
        val currentNotes = noteStorage.getAll().first()
        if (currentNotes.isEmpty()) {
            Log.d("NOTE_VIEW_MODEL", "Inserting default notes...")
            noteStorage.insertAll(Note.getNotes()).catch {
                Log.w("NOTE_VIEW_MODEL", "Could not insert default notes")
            }.collect{
                Log.d("NOTE_VIEW_MODEL", "Default notes inserted successfully")
                _notes.emit(Note.getNotes())
            }
        }
    }

    fun createNote(title: String, content: String) = viewModelScope.launch {
        val note = Note(
            id = Random.nextInt(0, Int.MAX_VALUE),
            title = title,
            content = content,
            timestamp = System.currentTimeMillis(),
            isArchived = false
        )
        noteStorage.insert(note).catch { Log.e("NOTE_VIEW_MODEL", "Could not insert note") }.collect {

        }
        noteStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }.collect{_notes.emit(it)}
    }
}