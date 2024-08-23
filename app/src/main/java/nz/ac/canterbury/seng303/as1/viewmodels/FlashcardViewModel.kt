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

    private val _shuffledFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val shuffledFlashcards: StateFlow<List<Flashcard>> get() = _shuffledFlashcards


    private val _selectedFlashcard = MutableStateFlow<Flashcard?>(null)
    val selectedFlashcard: StateFlow<Flashcard?> = _selectedFlashcard

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> get() = _currentIndex

    private val _selectedAnswer = MutableStateFlow<Answer?>(null)
    val selectedAnswer: StateFlow<Answer?> get() = _selectedAnswer

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> get() = _showResult

    private val _showEndScreen = MutableStateFlow(false)
    val showEndScreen: StateFlow<Boolean> get() = _showEndScreen

    private fun shuffleFlashcards() {
        _shuffledFlashcards.value = _flashcards.value.shuffled(Random)
    }

    fun getFlashcardById(flashcardId: Int?) = viewModelScope.launch{
        if (flashcardId != null) {
            _selectedFlashcard.value = flashcardStorage.get {it.getIdentifier() == flashcardId}.first()
        } else {
            _selectedFlashcard.value = null
        }
    }

    fun getFlashcards() = viewModelScope.launch {
        flashcardStorage.getAll().catch {
            Log.e("FLASHCARD_VIEW_MODEL", it.toString())
        }.collect{_flashcards.emit(it)}
    }

    fun getShuffledFlashcards() = viewModelScope.launch {
        flashcardStorage.getAll().catch {
            Log.e("FLASHCARD_VIEW_MODEL", it.toString())
        }.collect{
            _flashcards.emit(it)
            shuffleFlashcards()
        }
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

    fun deleteFlashcardById(flashcardId: Int?) = viewModelScope.launch {
        Log.d("FLASHCARD_VIEW_MODEL", "Deleting flashcard: $flashcardId")
        if (flashcardId != null) {
            flashcardStorage.delete(flashcardId).collect{}
            flashcardStorage.getAll().catch {
                Log.e("FLASHCARD_VIEW_MODEL", it.toString())
            }.collect { _flashcards.emit(it) }
        }
    }

    fun editFlashcardById(flashcardId: Int?, flashcard: Flashcard) = viewModelScope.launch {
        Log.d("FLASHCARD_VIEW_MODEL", "Editing flashcard: $flashcardId")
        if (flashcardId != null) {
            flashcardStorage.edit(flashcardId, flashcard).collect{}
            flashcardStorage.getAll().catch { Log.e("FLASHCARD_VIEW_MODEL", it.toString()) }
                .collect { _flashcards.emit(it) }
        }
    }

    fun selectAnswer(answer: Answer) {
        if (!_showResult.value) {
            _selectedAnswer.value = answer
        }
    }

    fun submitAnswer() {
        _showResult.value = true
        _selectedAnswer.value?.let {
            if (it.isCorrect) {
                _score.value += 1
            }
        }
    }

    fun nextFlashcard() {
        val current = _currentIndex.value
        if (current < _flashcards.value.size - 1) {
            _currentIndex.value = current + 1
            _selectedAnswer.value = null
            _showResult.value = false
        } else {
            _showEndScreen.value = true
        }
    }

    fun restartGame() {
        _currentIndex.value = 0
        _selectedAnswer.value = null
        _score.value = 0
        _showResult.value = false
        _showEndScreen.value = false
    }

}