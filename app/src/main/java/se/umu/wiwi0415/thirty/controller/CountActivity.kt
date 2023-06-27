package se.umu.wiwi0415.thirty.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.View
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
    //private lateinit var scoreModel: Score
    private var scoreSnapshot: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        //Init
        super.onCreate(savedInstanceState)
        binding = ActivityCountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val CountImageViews = arrayOf(
            binding.countTopLeft,
            binding.countTopMid,
            binding.countTopRight,
            binding.countBottomLeft,
            binding.countBottomMid,
            binding.countBottomRight
        )

        vm.setCountImages(CountImageViews)

//         Retrieve diceModel from intent
//         Version 34 seemed to remove too many available devices, workaround for deprecation;
//         https://stackoverflow.com/questions/73019160/android-getparcelableextra-deprecated
        vm.diceModel = when {
            SDK_INT >= 34 -> intent.getParcelableExtra(DICE_MODEL, SetOfDice::class.java)!!
            else -> (@Suppress("DEPRECATION") intent.getParcelableExtra(DICE_MODEL) as? SetOfDice)!!
        }

        //TODO: VIEWMODEL
        vm.updateCountImages()

        //Retrieve spinner choice from intent
        val spinnerValue = intent.getStringExtra(SPINNER_VALUE)
        //TODO: Move to viewModel
        binding.choiceText.text = spinnerValue

        //Retrieve score from intent
        vm.scoreModel = when {
            SDK_INT >= 34 -> intent.getParcelableExtra(SCORE_MODEL, Score::class.java)!!
            else -> (@Suppress("DEPRECATION") intent.getParcelableExtra(SCORE_MODEL) as? Score)!!
        }
        scoreSnapshot = vm.scoreModel.totalScore
        binding.scoreView.text = vm.scoreModel.displayScoreCount()

        println("Welcome to Count Activity! Recieved dicemodel: ${vm.diceModel}")

        //Buttons
        binding.countTopLeft.setOnClickListener { view: View ->
            println("top left dice clicked!")
            val clickedDice = vm.diceModel.diceList[0]
            vm.selectDice(clickedDice)
        }

        binding.countTopMid.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[1]
            vm.selectDice(clickedDice)
        }

        binding.countTopRight.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[2]
            vm.selectDice(clickedDice)
        }

        binding.countBottomLeft.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[3]
            vm.selectDice(clickedDice)
        }

        binding.countBottomMid.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[4]
            vm.selectDice(clickedDice)
        }

        binding.countBottomRight.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[5]
            vm.selectDice(clickedDice)
        }

        //TODO: VIEWMODEL
        binding.addButton.setOnClickListener { view: View ->
            if (spinnerValue != null) {
                vm.scoreModel.countScore(vm.diceModel, spinnerValue)
            }
            binding.scoreView.text = vm.scoreModel.displayScoreCount()
            vm.diceModel.unSelectAllDice()
            vm.updateCountImages()
        }

        binding.nextButton.setOnClickListener { view: View ->
            //Start new round activity, send score
            vm.scoreModel.lastRoundScore = vm.scoreModel.totalScore - scoreSnapshot
            setScoreResult(vm.scoreModel)
            finish()
        }

        println("END OF ONCREATE IN COUNTACTIVITY")
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