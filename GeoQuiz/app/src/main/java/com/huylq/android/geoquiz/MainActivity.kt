package com.huylq.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CORRECT_COUNT = "correct_count"
private const val KEY_ANSWERED_COUNT = "answered_count"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val correctAnswerCount = savedInstanceState?.getInt(KEY_CORRECT_COUNT, 0) ?: 0
        quizViewModel.correctAnswerCount = correctAnswerCount
        val answeredCount = savedInstanceState?.getInt(KEY_ANSWERED_COUNT, 0) ?: 0
        quizViewModel.answeredCount = answeredCount

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        updateQuestion()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CORRECT_COUNT, quizViewModel.correctAnswerCount)
        outState.putInt(KEY_ANSWERED_COUNT, quizViewModel.answeredCount)
    }

    private fun markQuestionAsAnswered() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        quizViewModel.isCurrentQuestionAnswered = true
    }

    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionText)
        val isQuestionAnswered = quizViewModel.isCurrentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        markQuestionAsAnswered()
        quizViewModel.answeredCount++
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (userAnswer == correctAnswer) {
            quizViewModel.correctAnswerCount++
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        if (quizViewModel.answeredCount == quizViewModel.questionCount) {
            val score = (quizViewModel.correctAnswerCount * 100 / quizViewModel.questionCount)
            Toast.makeText(this, "Finish! You were correct $score% of all questions", Toast.LENGTH_LONG).show()
        }
    }
}