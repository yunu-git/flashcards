package nz.ac.canterbury.seng303.as1.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.as1.models.Answer
import nz.ac.canterbury.seng303.as1.models.Flashcard

class EditFlashcardViewModel(): ViewModel() {
    var term by mutableStateOf("")
        private set

    var answers by mutableStateOf(listOf<Answer>(Answer("", false), Answer("", false)))
        private set

    fun updateTerm(newTerm: String) {
        term = newTerm
    }

    fun updateAnswer(index: Int, newAnswer: Answer) {
        if (index in answers.indices) {
            answers = answers.toMutableList().apply { set(index, newAnswer) }
        }
    }

    fun updateAnswers(newAnswers: List<Answer>) {
        answers = newAnswers;
    }

    fun addAnswer(answer: Answer) {
        answers = answers.toMutableList().apply { add(answer) }
    }

    fun removeAnswer(index: Int) {
        if (index in answers.indices) {
            answers = answers.toMutableList().apply { removeAt(index) }
        }
    }

    fun updateCorrect(index: Int, selected: Boolean) {
        answers = answers.mapIndexed { i, answer ->
            if (i == index) {
                answer.copy(isCorrect = selected)
            } else {
                answer.copy(isCorrect = false)
            }
        }
    }

    fun setDefaultValues(selectedFlashcard: Flashcard?) {
        selectedFlashcard?.let {
            term = it.term
            answers = it.answers
        }
    }
}