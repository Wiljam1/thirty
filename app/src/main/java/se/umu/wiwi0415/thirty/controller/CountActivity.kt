package se.umu.wiwi0415.thirty.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import se.umu.wiwi0415.thirty.databinding.ActivityCountBinding
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.model.SetOfDice
import se.umu.wiwi0415.thirty.view.CountViewModel

private const val DICE_MODEL = "se.umu.wiwi0415.thirty.dice_model"
private const val SPINNER_VALUE = "se.umu.wiwi0415.thirty.spinner_value"
const val SCORE_MODEL = "se.umu.wiwi0415.thirty.score_model"

class CountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountBinding
    private val vm: CountViewModel by viewModels ()
    private var scoreSnapshot: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        //Init
        super.onCreate(savedInstanceState)
        binding = ActivityCountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countImageViews = arrayOf(
            binding.countTopLeft,
            binding.countTopMid,
            binding.countTopRight,
            binding.countBottomLeft,
            binding.countBottomMid,
            binding.countBottomRight
        )
        vm.setCountImages(countImageViews)

//      Retrieve diceModel from intent
//      Version 34 seemed to remove too many available devices, workaround for deprecation;
//      https://stackoverflow.com/questions/73019160/android-getparcelableextra-deprecated
        vm.diceModel = when {
            SDK_INT >= 34 -> intent.getParcelableExtra(DICE_MODEL, SetOfDice::class.java)!!
            else -> (@Suppress("DEPRECATION") intent.getParcelableExtra(DICE_MODEL) as? SetOfDice)!!
        }

        //Retrieve spinner choice from intent
        val spinnerValue = intent.getStringExtra(SPINNER_VALUE)
        binding.choiceText.text = spinnerValue

        //Retrieve score from intent
        vm.scoreModel = when {
            SDK_INT >= 34 -> intent.getParcelableExtra(SCORE_MODEL, Score::class.java)!!
            else -> (@Suppress("DEPRECATION") intent.getParcelableExtra(SCORE_MODEL) as? Score)!!
        }

        scoreSnapshot = vm.scoreModel.totalScore
        if(spinnerValue != null)
            binding.scoreView.text = vm.scoreModel.displayScoreCount(spinnerValue)

        vm.updateCountImages()

        //Buttons
        binding.countTopLeft.setOnClickListener {
            vm.selectDice(0)
        }

        binding.countTopMid.setOnClickListener {
            vm.selectDice(1)
        }

        binding.countTopRight.setOnClickListener {
            vm.selectDice(2)
        }

        binding.countBottomLeft.setOnClickListener {
            vm.selectDice(3)
        }

        binding.countBottomMid.setOnClickListener {
            vm.selectDice(4)
        }

        binding.countBottomRight.setOnClickListener {
            vm.selectDice(5)
        }

        binding.addButton.setOnClickListener {
            if (spinnerValue != null) {
                vm.countScore(spinnerValue)
                binding.scoreView.text = vm.scoreModel.displayScoreCount(spinnerValue)
            }

            vm.unSelectAllDice()
            vm.updateCountImages()
        }

        binding.nextButton.setOnClickListener {
            //Start new round activity, send score back to MainActivity
            vm.updateLastRoundScore(scoreSnapshot)
            setScoreResult(vm.scoreModel)
            finish()
        }

    }

    private fun setScoreResult(scoreModel: Score) {
        val data = Intent().apply {
            putExtra(SCORE_MODEL, scoreModel)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, diceModel: SetOfDice, spinnerValue: String, scoreModel: Score): Intent {
            return Intent(packageContext, CountActivity::class.java) .apply {
                putExtra(DICE_MODEL, diceModel)
                putExtra(SPINNER_VALUE, spinnerValue)
                putExtra(SCORE_MODEL, scoreModel)
            }
        }
    }
}