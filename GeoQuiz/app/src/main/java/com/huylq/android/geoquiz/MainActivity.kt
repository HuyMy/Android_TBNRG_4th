package com.huylq.android.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CORRECT_COUNT = "correct_count"
private const val KEY_ANSWERED_COUNT = "answered_count"
private const val KEY_CHEAT_REMAINING = "cheat_remaining"
const val MAX_CHEAT_ALLOWANCE = 3

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatRemainingTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (!quizViewModel.isQuestionCheated) {
                val data = it.data
                quizViewModel.isQuestionCheated = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
                        ?: false
            }

            if (quizViewModel.isQuestionCheated) {
                quizViewModel.cheatAllowanceRemain--
                bindCheatLayout()
                showRespond(R.string.judgement_toast)
            }
        }
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
        val cheatRemaining = savedInstanceState?.getInt(KEY_CHEAT_REMAINING, MAX_CHEAT_ALLOWANCE)
                ?: MAX_CHEAT_ALLOWANCE
        quizViewModel.cheatAllowanceRemain = cheatRemaining

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatRemainingTextView = findViewById(R.id.cheat_remaining_text_view)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startForResult.launch(intent)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        bindCheatLayout()
        updateQuestion()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CORRECT_COUNT, quizViewModel.correctAnswerCount)
        outState.putInt(KEY_ANSWERED_COUNT, quizViewModel.answeredCount)
        outState.putInt(KEY_CHEAT_REMAINING, quizViewModel.cheatAllowanceRemain)
    }

    private fun markQuestionAsAnswered() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        quizViewModel.isCurrentQuestionAnswered = true
        quizViewModel.answeredCount++
    }

    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionText)
        val isQuestionAnswered = quizViewModel.isCurrentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (userAnswer == correctAnswer) {
            quizViewModel.correctAnswerCount++
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        showRespond(messageResId)
    }

    private fun showRespond(@StringRes messageResId: Int) {
        markQuestionAsAnswered()
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        if (quizViewModel.answeredCount == quizViewModel.questionCount) {
            val score = (quizViewModel.correctAnswerCount * 100 / quizViewModel.questionCount)
            Toast.makeText(this, "Finish! You were correct $score% of all questions", Toast.LENGTH_LONG).show()
        }
    }

    private fun bindCheatLayout() {
        cheatRemainingTextView.text = resources.getQuantityString(
                R.plurals.cheat_remaining,
                quizViewModel.cheatAllowanceRemain,
                quizViewModel.cheatAllowanceRemain
        )
        cheatButton.isEnabled = quizViewModel.cheatAllowanceRemain != 0
    }
}