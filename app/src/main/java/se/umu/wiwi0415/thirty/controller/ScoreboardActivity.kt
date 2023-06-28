package se.umu.wiwi0415.thirty.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import se.umu.wiwi0415.thirty.R
import se.umu.wiwi0415.thirty.databinding.ActivityScoreboardBinding
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.view.ScoreboardViewModel

class ScoreboardActivity : AppCompatActivity() {

    companion object {

        fun newIntent(context: Context, scoreModel: Score): Intent {
            return Intent(context, ScoreboardActivity::class.java).apply {
                putExtra(SCORE_MODEL, scoreModel)
            }
        }
    }

    private lateinit var binding: ActivityScoreboardBinding
    private val vm: ScoreboardViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve scoreModel
        vm.scoreModel = when {
            Build.VERSION.SDK_INT >= 34 -> intent.getParcelableExtra(SCORE_MODEL, Score::class.java)!!
            else -> (@Suppress("DEPRECATION") intent.getParcelableExtra(SCORE_MODEL) as? Score)!!
        }
        val scoreText = resources.getString(R.string.finished_text, vm.scoreModel.totalScore)
        binding.scoreTextView.text = scoreText

        binding.scoreboardTextView.text = vm.scoreModel.displayScoreboard()

        binding.backButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}