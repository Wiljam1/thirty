package se.umu.wiwi0415.thirty.view

import android.widget.ImageView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.wiwi0415.thirty.model.Dice
import se.umu.wiwi0415.thirty.model.Score
import se.umu.wiwi0415.thirty.model.SetOfDice

const val COUNT_DICE_MODEL_KEY = "COUNT_DICE_MODEL_KEY"
const val COUNT_SCORE_MODEL_KEY = "COUNT_SCORE_MODEL_KEY"

class CountViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private lateinit var countImageViews: Array<ImageView>

    var diceModel: SetOfDice
        get() = savedStateHandle[COUNT_DICE_MODEL_KEY] ?: SetOfDice()
        set(value) = savedStateHandle.set(COUNT_DICE_MODEL_KEY, value)

    var scoreModel: Score
        get() = savedStateHandle[COUNT_SCORE_MODEL_KEY] ?: Score(0, 0,0)
        set(value) = savedStateHandle.set(COUNT_SCORE_MODEL_KEY, value)

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

    fun selectDice(clickedDice: Dice) {
        diceModel.toggleSelected(clickedDice)
        updateCountImages()
    }

}