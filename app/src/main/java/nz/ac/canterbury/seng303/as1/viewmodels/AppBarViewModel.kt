package nz.ac.canterbury.seng303.as1.viewmodels

import androidx.lifecycle.ViewModel

class AppBarViewModel: ViewModel() {
    private val idNameMap: HashMap<String, String> = HashMap()

    fun getNameById(id: String): String? {
        return idNameMap[id]
    }

    fun init() {
        idNameMap["Home"] = "Home"
        idNameMap["FlashcardList"] = "View Flashcards"
        idNameMap["CreateFlashcard"] = "Create a Flashcard"
        idNameMap["EditFlashcard/{flashcardId}"] = "Edit Flashcard"
        idNameMap["viewFlashcard/{flashcardId}"] = "Flashcard"
        idNameMap["PlayFlashcards"] = "Play Flashcards"
    }
}