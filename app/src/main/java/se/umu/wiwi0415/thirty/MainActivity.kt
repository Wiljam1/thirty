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
import se.umu.wiwi0415.thirty.controller.ScoreActivity
import se.umu.wiwi0415.thirty.databinding.ActivityMainBinding
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.view.DiceViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: DiceViewModel by viewModels()

    private val countLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //TODO: Might not be needed
            vm.scoreModel = when {
                Build.VERSION.SDK_INT >= 34 -> result.data?.getParcelableExtra(SCORE_MODEL, Score::class.java)!!
                else -> (@Suppress("DEPRECATION") result.data?.getParcelableExtra(SCORE_MODEL) as? Score)!!
            }
            //TODO: Do in viewmodel
            binding.scoreView.text = vm.scoreModel.displayScoreRoll()
            binding.countButton.isEnabled = false
            binding.throwButton.isEnabled = true

            vm.diceModel.unSelectAllDice()
            vm.diceModel.disableDice()
            if(vm.nextRound() == 3) {
                //Launch new activity, showing score
                val intent = ScoreActivity.newIntent(this@MainActivity, vm.scoreModel.totalScore)
                scoreLauncher.launch(intent)
            }

            //TODO: VIEWMODEL
            binding.gameView.text = vm.gameState.displayGameState()
            vm.updateMainImages()
        }
    }

    private val scoreLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Reset after showing score
            reset()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Init
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //TODO: Do in viewmodel
        binding.countButton.isEnabled = false
        binding.throwButton.isEnabled = true
        binding.scoreView.text = vm.scoreModel.displayScoreRoll()
        binding.gameView.text = vm.gameState.displayGameState()

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
        //TODO: Alla knappar kan fixas snyggare med VM
        binding.diceTopLeft.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[0]
            vm.selectDice(clickedDice)
        }

        binding.diceTopMid.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[1]
            vm.selectDice(clickedDice)
        }

        binding.diceTopRight.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[2]
            vm.selectDice(clickedDice)
        }

        binding.diceBottomLeft.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[3]
            vm.selectDice(clickedDice)
        }

        binding.diceBottomMid.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[4]
            vm.selectDice(clickedDice)
        }

        binding.diceBottomRight.setOnClickListener { view: View ->
            val clickedDice = vm.diceModel.diceList[5]
            vm.selectDice(clickedDice)
        }

        binding.throwButton.setOnClickListener { view: View ->
            if(!binding.countButton.isEnabled)
                binding.countButton.isEnabled = true

            vm.throwDice()
            if(vm.incrementThrows() == 3) {
                binding.throwButton.isEnabled = false
            }
            //TODO: Fixa till viewmodel
            binding.gameView.text = vm.gameState.displayGameState()
        }

        binding.countButton.setOnClickListener { view: View ->
            //go to new activity to count points
            vm.diceModel.unSelectAllDice()
            println("Trying to open countActivity..")
            //TODO: Create a function in viewmodel that does this...
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

    private fun reset() {
        println("RESET CALLED!")
        vm.reset()
        resetSpinner()

        binding.scoreView.text = vm.scoreModel.displayScoreRoll()
        binding.gameView.text = vm.gameState.displayGameState()
        vm.updateMainImages()
    }

    private fun resetSpinner() {
        val adapter = binding.choiceSpinner.adapter as ArrayAdapter<*>
        val newAdapter = ArrayAdapter(binding.choiceSpinner.context, android.R.layout.simple_spinner_item, vm.initialSpinnerList)
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.choiceSpinner.adapter = newAdapter
        adapter.notifyDataSetChanged() //maybe not needed

        vm.spinnerItems = emptyList()
    }

    //TODO: ViewModel function?
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