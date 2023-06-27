package se.umu.wiwi0415.thirty.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import se.umu.wiwi0415.thirty.R
import se.umu.wiwi0415.thirty.databinding.ActivityScoreBinding

class ScoreActivity : AppCompatActivity() {

    companion object {
        private const val TOTAL_SCORE_KEY = "TOTAL_SCORE_KEY"

        fun newIntent(context: Context, totalScore: Int): Intent {
            return Intent(context, ScoreActivity::class.java).apply {
                putExtra(TOTAL_SCORE_KEY, totalScore)
            }
        }
    }

    private lateinit var binding: ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalScore = intent.getIntExtra(TOTAL_SCORE_KEY, 0)
        val scoreText = resources.getString(R.string.finished_text, totalScore)
        binding.scoreTextView.text = scoreText

        binding.backButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}