package nz.ac.canterbury.seng303.as1.viewmodels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard

class PlayFlashcardsViewModel : ViewModel() {

    var currentIndex by mutableIntStateOf(0)
    var selectedAnswer by mutableStateOf<Answer?>(null)
    var score by mutableIntStateOf(0)
    var showResult by mutableStateOf(false)
    var showEndScreen by mutableStateOf(false)

    var flashcards by mutableStateOf<List<Flashcard>>(emptyList())

    fun nextFlashcard() {
        if (currentIndex < flashcards.size - 1) {
            currentIndex++
            selectedAnswer = null
            showResult = false
        } else {
            showEndScreen = true
        }
    }

    fun submitAnswer() {
        showResult = true
        if (selectedAnswer?.isCorrect == true) {
            score++
        }
    }

    fun restartGame() {
        currentIndex = 0
        selectedAnswer = null
        score = 0
        showResult = false
        showEndScreen = false
    }

    fun setFlashcards(newFlashcards: List<Flashcard>) {
        flashcards = newFlashcards
    }
}
