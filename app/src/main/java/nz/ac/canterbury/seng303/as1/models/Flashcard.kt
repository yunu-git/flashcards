package nz.ac.canterbury.seng303.as1.models

data class Answer(
    val text: String,
    val isCorrect: Boolean
)

data class Flashcard(
    val id: Int,
    val term: String,
    val answers: List<Answer>
): Identifiable {
   companion object {
       fun getTestFlashcards(): List<Flashcard> {
           return listOf(
               Flashcard(
                   id = 1,
                   term = "What should be discussed in the meeting?",
                   answers = listOf(
                       Answer(text = "Discuss project updates and future plans.", isCorrect = true),
                       Answer(text = "Plan a team outing.", isCorrect = false),
                       Answer(text = "Review last month's expenses.", isCorrect = false),
                       Answer(text = "Discuss vacation plans.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 2,
                   term = "What is included in the shopping list?",
                   answers = listOf(
                       Answer(text = "Milk, eggs, bread, and coffee.", isCorrect = true),
                       Answer(text = "Fruits, vegetables, and snacks.", isCorrect = false),
                       Answer(text = "Books and stationery.", isCorrect = false),
                       Answer(text = "Electronics and gadgets.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 3,
                   term = "When is the project deadline?",
                   answers = listOf(
                       Answer(text = "Submit the final report by Friday.", isCorrect = true),
                       Answer(text = "Complete the initial draft by Monday.", isCorrect = false),
                       Answer(text = "Schedule a team meeting next week.", isCorrect = false),
                       Answer(text = "Finalize the project budget.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 4,
                   term = "What should be considered for birthday gift ideas?",
                   answers = listOf(
                       Answer(text = "Consider getting a book or a gadget.", isCorrect = true),
                       Answer(text = "Purchase a new wardrobe.", isCorrect = false),
                       Answer(text = "Plan a surprise party.", isCorrect = false),
                       Answer(text = "Get a subscription to a magazine.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 5,
                   term = "What are some book recommendations?",
                   answers = listOf(
                       Answer(text = "Check out 'The Great Gatsby' and 'To Kill a Mockingbird'.", isCorrect = true),
                       Answer(text = "Read 'Moby Dick' and '1984'.", isCorrect = false),
                       Answer(text = "Explore 'The Catcher in the Rye' and 'Brave New World'.", isCorrect = false),
                       Answer(text = "Consider 'Pride and Prejudice' and 'Jane Eyre'.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 6,
                   term = "What is planned for dinner?",
                   answers = listOf(
                       Answer(text = "Go out to dinner with the boys.", isCorrect = true),
                       Answer(text = "Cook a homemade meal.", isCorrect = false),
                       Answer(text = "Order takeout from a restaurant.", isCorrect = false),
                       Answer(text = "Have a picnic in the park.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 7,
                   term = "What should be done for vacation plans?",
                   answers = listOf(
                       Answer(text = "Book flights, reserve hotels, and create itinerary.", isCorrect = true),
                       Answer(text = "Choose travel outfits and pack bags.", isCorrect = false),
                       Answer(text = "Check local weather forecasts.", isCorrect = false),
                       Answer(text = "Get travel insurance and exchange currency.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 8,
                   term = "What are some fitness goals?",
                   answers = listOf(
                       Answer(text = "Set workout schedule and plan healthy meals.", isCorrect = true),
                       Answer(text = "Join a local gym and hire a personal trainer.", isCorrect = false),
                       Answer(text = "Participate in a fitness challenge.", isCorrect = false),
                       Answer(text = "Track daily steps with a fitness tracker.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 9,
                   term = "What should be prepared for the project kickoff?",
                   answers = listOf(
                       Answer(text = "Prepare presentation and gather project team.", isCorrect = true),
                       Answer(text = "Draft the project proposal and budget.", isCorrect = false),
                       Answer(text = "Conduct market research and competitor analysis.", isCorrect = false),
                       Answer(text = "Develop a detailed project plan.", isCorrect = false)
                   )
               ),
               Flashcard(
                   id = 10,
                   term = "What should be done for gardening?",
                   answers = listOf(
                       Answer(text = "Buy seeds, plant flowers, and water garden regularly.", isCorrect = true),
                       Answer(text = "Create a compost bin and mulch garden beds.", isCorrect = false),
                       Answer(text = "Prune trees and shrubs.", isCorrect = false),
                       Answer(text = "Install irrigation systems and garden lighting.", isCorrect = false)
                   )
               )
           )
       }
   }

    override fun getIdentifier(): Int {
        return id;
    }
}