package nz.ac.canterbury.seng303.as1.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.as1.models.Answer


class CreateFlashcardViewModel : ViewModel() {
    var term by mutableStateOf("")
        private set

    var answers by mutableStateOf(listOf<Answer>())
        private set

    fun updateTerm(newTerm: String) {
        term = newTerm
    }

    fun updateAnswer(index: Int, newAnswer: Answer) {
        if (index in answers.indices) {
            answers = answers.toMutableList().apply { set(index, newAnswer) }
        }
    }

    fun addAnswer(answer: Answer) {
        answers = answers.toMutableList().apply { add(answer) }
    }

    fun removeAnswer(index: Int) {
        if (index in answers.indices) {
            answers = answers.toMutableList().apply { removeAt(index) }
        }
    }

}