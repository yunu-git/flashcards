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
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard
import kotlin.random.Random

class FlashcardViewModel(private val flashcardStorage: Storage<Flashcard>): ViewModel() {
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList());
    val flashcards: StateFlow<List<Flashcard>> get() = _flashcards

    private val _selectedFlashcard = MutableStateFlow<Flashcard?>(null)
    val selectedFlashcard: StateFlow<Flashcard?> = _selectedFlashcard
    fun getFlashcardById(noteId: Int?) = viewModelScope.launch{
        if (noteId != null) {
            _selectedFlashcard.value = flashcardStorage.get {it.getIdentifier() == noteId}.first()
        } else {
            _selectedFlashcard.value = null
        }
    }

    fun getFlashcards() = viewModelScope.launch {
        flashcardStorage.getAll().catch {
            Log.e("FLASHCARD_VIEW_MODEL", it.toString())
        }.collect{_flashcards.emit(it)}
    }

    fun loadDefaultFlashcardsForTestingPurposes() = viewModelScope.launch {
        val currentFlashcards = flashcardStorage.getAll().first()
        if (currentFlashcards.isEmpty()) {
            Log.d("FLASHCARD_VIEW_MODEL", "Inserting default flashcards")
            flashcardStorage.insertAll(Flashcard.getTestFlashcards()).catch {
                Log.w("FLASHCARD_VIEW_MODEL", "could not insert default flashcards")
            }.collect{
                Log.d("FLASHCARD_VIEW_MODEL", "Default flashcards inserted successfully")
                _flashcards.emit(Flashcard.getTestFlashcards())
            }
        }
    }

    fun createFlashcard(term: String, definitions: List<Answer>) = viewModelScope.launch {
        val flashcard = Flashcard(
            id = Random.nextInt(0, Int.MAX_VALUE),
            term = term,
            answers = definitions
        )
        flashcardStorage.insert(flashcard).catch {
            Log.e("FLASHCARD_VIEW_MODEL", "Could not insert flashcard")
        }.collect{}
        flashcardStorage.getAll().catch {
            Log.e("FLASHCARD_VIEW_MODEL", it.toString())
        }.collect{_flashcards.emit(it)}
    }
}