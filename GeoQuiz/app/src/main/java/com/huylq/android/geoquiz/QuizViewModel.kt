package com.huylq.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, answer = true, isAnswered = false),
        Question(R.string.question_oceans, answer = true, isAnswered = false),
        Question(R.string.question_mideast, answer = false, isAnswered = false),
        Question(R.string.question_africa, answer = false, isAnswered = false),
        Question(R.string.question_americas, answer = true, isAnswered = false),
        Question(R.string.question_asia, answer = true, isAnswered = false)
    )

    var isCheater = false
    var currentIndex = 0
    var answeredCount = 0

    val questionCount: Int
        get() = questionBank.size

    var correctAnswerCount = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var isCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered
        set(value) {
            questionBank[currentIndex].isAnswered = value
        }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }
}