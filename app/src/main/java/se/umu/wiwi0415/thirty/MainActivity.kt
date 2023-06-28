package se.umu.wiwi0415.thirty

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import se.umu.wiwi0415.thirty.controller.CountActivity
import se.umu.wiwi0415.thirty.controller.SCORE_MODEL
import se.umu.wiwi0415.thirty.controller.ScoreboardActivity
import se.umu.wiwi0415.thirty.databinding.ActivityMainBinding
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.view.DiceViewModel

// Ten rounds to allow every Spinner choice once.
const val ALLOWED_ROUNDS = 10

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: DiceViewModel by viewModels()

    private val countLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Retrieve scoreModel from CountActivity
            vm.scoreModel = when {
                Build.VERSION.SDK_INT >= 34 -> result.data?.getParcelableExtra(SCORE_MODEL, Score::class.java)!!
                else -> (@Suppress("DEPRECATION") result.data?.getParcelableExtra(SCORE_MODEL) as? Score)!!
            }

            if(vm.nextRound() > ALLOWED_ROUNDS) {
                //Launch new activity, showing score and steps
                val intent = ScoreboardActivity.newIntent(this@MainActivity, vm.scoreModel)
                scoreLauncher.launch(intent)
            }

            refreshUI()
            vm.diceModel.unSelectAllDice()
            vm.diceModel.disableDice()
            vm.updateMainImages()
        }
    }

    private val scoreLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Reset application after showing score
            reset()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Init
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Give references of ImageViews to VM
        val diceImageViews = arrayOf(
            binding.diceTopLeft,
            binding.diceTopMid,
            binding.diceTopRight,
            binding.diceBottomLeft,
            binding.diceBottomMid,
            binding.diceBottomRight
        )
        vm.setMainImages(diceImageViews)
        vm.updateMainImages()

        refreshUI()

        //Keep initial spinner
        if(vm.initialSpinnerList.isEmpty()) {
            val adapter = binding.choiceSpinner.adapter as ArrayAdapter<*>
            val initList = mutableListOf<String>()
            for(i in 0 until adapter.count) {
                initList.add(adapter.getItem(i)!! as String)
            }
            vm.initialSpinnerList = initList
        }

        //Retrieve saved Spinner items from VM
        val savedSpinnerItems = vm.spinnerItems
        if(savedSpinnerItems.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, savedSpinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.choiceSpinner.adapter = adapter
            adapter.notifyDataSetChanged() //maybe not needed
        }

        //Buttons
        binding.diceTopLeft.setOnClickListener {
            vm.selectDice(0)
        }

        binding.diceTopMid.setOnClickListener {
            vm.selectDice(1)
        }

        binding.diceTopRight.setOnClickListener {
            vm.selectDice(2)
        }

        binding.diceBottomLeft.setOnClickListener {
            vm.selectDice(3)
        }

        binding.diceBottomMid.setOnClickListener {
            vm.selectDice(4)
        }

        binding.diceBottomRight.setOnClickListener {
            vm.selectDice(5)
        }

        binding.throwButton.setOnClickListener {
            if(!binding.countButton.isEnabled)
                binding.countButton.isEnabled = true

            vm.throwDice()
            if(vm.incrementThrows() == 3) {
                binding.throwButton.isEnabled = false
            }

            binding.gameView.text = vm.displayGameState()
        }

        binding.countButton.setOnClickListener { view: View ->
            vm.diceModel.unSelectAllDice()

            //Launch CountActivity
            val spinnerValue = binding.choiceSpinner.selectedItem.toString()
            removeSpinnerItem(spinnerValue)
            val intent = CountActivity.newIntent(
                this@MainActivity,
                vm.diceModel,
                spinnerValue,
                vm.scoreModel
            )
            countLauncher.launch(intent)
        }

    }

    private fun refreshUI() {
        binding.scoreView.text = vm.displayScoreRoll()
        binding.gameView.text = vm.displayGameState()
        binding.countButton.isEnabled = false
        binding.throwButton.isEnabled = true
    }

    private fun reset() {
        vm.reset()
        resetSpinner()

        binding.scoreView.text = vm.displayScoreRoll()
        binding.gameView.text = vm.displayGameState()
        vm.updateMainImages()
    }

    private fun resetSpinner() {
        val adapter = binding.choiceSpinner.adapter as ArrayAdapter<*>
        val newAdapter = ArrayAdapter(binding.choiceSpinner.context, android.R.layout.simple_spinner_item, vm.initialSpinnerList)
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.choiceSpinner.adapter = newAdapter
        adapter.notifyDataSetChanged()

        vm.spinnerItems = emptyList()
    }

    private fun removeSpinnerItem(itemToRemove: String) {
        val adapter = binding.choiceSpinner.adapter as ArrayAdapter<*>
        val items = mutableListOf<String>()
        for(i in 0 until adapter.count) {
            items.add(adapter.getItem(i)!! as String)
        }
        items.remove(itemToRemove)
        val newAdapter = ArrayAdapter(binding.choiceSpinner.context, android.R.layout.simple_spinner_item, items)
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.choiceSpinner.adapter = newAdapter
        adapter.notifyDataSetChanged()

        //Save new state of items
        vm.spinnerItems = items
    }
}