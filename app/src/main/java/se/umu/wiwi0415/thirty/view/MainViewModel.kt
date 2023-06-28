package se.umu.wiwi0415.thirty.view

import android.widget.ImageView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.wiwi0415.thirty.model.GameState
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.model.SetOfDice

const val GAME_STATE_KEY = "GAME_STATE_KEY"
const val DICE_MODEL_KEY = "DICE_MODEL_KEY"
const val SCORE_MODEL_KEY = "SCORE_MODEL_KEY"
const val SPINNER_ITEMS_KEY = "SPINNER_ITEMS_KEY"
const val INITIAL_SPINNER_LIST_KEY = "INITIAL_SPINNER_LIST_KEY"

class DiceViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private lateinit var diceImageViews: Array<ImageView>

    var initialSpinnerList: List<String>
        get() = savedStateHandle[INITIAL_SPINNER_LIST_KEY] ?: emptyList()
        set(value) = savedStateHandle.set(INITIAL_SPINNER_LIST_KEY, value)

    var spinnerItems: List<String>
        get() = savedStateHandle[SPINNER_ITEMS_KEY] ?: emptyList()
        set(value) = savedStateHandle.set(SPINNER_ITEMS_KEY, value)

    private var gameState: GameState
        get() = savedStateHandle[GAME_STATE_KEY] ?: GameState()
        set(value) = savedStateHandle.set(GAME_STATE_KEY, value)

    var diceModel: SetOfDice
        get() = savedStateHandle[DICE_MODEL_KEY] ?: SetOfDice()
        set(value) = savedStateHandle.set(DICE_MODEL_KEY, value)

    var scoreModel: Score
        get() = savedStateHandle[SCORE_MODEL_KEY] ?: Score(0, 0,0)
        set(value) = savedStateHandle.set(SCORE_MODEL_KEY, value)

    fun setMainImages(imageViews: Array<ImageView>) {
        diceImageViews = imageViews
        updateMainImages()
    }

    fun updateMainImages() {
        diceModel.updateDrawables()
        val diceList = diceModel.diceList
        diceList.forEachIndexed { index, dice ->
            diceImageViews[index].setImageResource(dice.drawableResId)
        }
    }

    fun displayGameState(): String {
        return gameState.displayGameState()
    }

    fun displayScoreRoll(): String {
        return scoreModel.displayScoreRoll()
    }

    fun selectDice(diceId: Int) {
        val clickedDice = diceModel.diceList[diceId]
        diceModel.toggleSelected(clickedDice)
        updateMainImages()
    }

    fun throwDice() {
        val updatedDiceModel = diceModel.copy()
        updatedDiceModel.throwDice()
        diceModel = updatedDiceModel
        savedStateHandle[DICE_MODEL_KEY] = diceModel
        updateMainImages()
    }

    fun incrementThrows(): Int {
        val updatedGameState = gameState.copy(throws = gameState.throws + 1)
        savedStateHandle[GAME_STATE_KEY] = updatedGameState
        return updatedGameState.throws
    }

    fun nextRound(): Int {
        gameState.throws = 0
        gameState.round++
        return gameState.round
    }

     fun reset() {
        gameState.reset()
        diceModel.reset()
         scoreModel.reset()
    }

}