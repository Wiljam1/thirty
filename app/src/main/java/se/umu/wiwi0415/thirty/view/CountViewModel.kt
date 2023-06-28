package se.umu.wiwi0415.thirty.view

import android.widget.ImageView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.model.SetOfDice

class CountViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private lateinit var countImageViews: Array<ImageView>

    var diceModel: SetOfDice
        get() = savedStateHandle[DICE_MODEL_KEY] ?: SetOfDice()
        set(value) = savedStateHandle.set(DICE_MODEL_KEY, value)

    var scoreModel: Score
        get() = savedStateHandle[SCORE_MODEL_KEY] ?: Score(0, 0,0)
        set(value) = savedStateHandle.set(SCORE_MODEL_KEY, value)

    fun setCountImages(imageViews: Array<ImageView>) {
        countImageViews = imageViews
        updateCountImages()
    }

    fun updateCountImages() {
        diceModel.updateDrawables()
        val diceList = diceModel.diceList
        diceList.forEachIndexed { index, dice ->
            countImageViews[index].setImageResource(dice.drawableResId)
        }
    }

    fun selectDice(diceId: Int) {
        val clickedDice = diceModel.diceList[diceId]
        diceModel.toggleSelected(clickedDice)
        updateCountImages()
    }

    fun countScore(spinnerValue: String) {
        scoreModel.countScore(diceModel, spinnerValue)
    }

    fun unSelectAllDice() {
        diceModel.unSelectAllDice()
    }

    fun updateLastRoundScore(scoreSnapshot: Int) {
        scoreModel.lastRoundScore = scoreModel.totalScore - scoreSnapshot
    }

}