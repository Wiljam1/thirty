package se.umu.wiwi0415.thirty.model

import android.os.Parcel
import android.os.Parcelable
import se.umu.wiwi0415.thirty.R
import kotlin.random.Random

data class SetOfDice(
    var diceList: List<Dice> = listOf(
        Dice(0, R.drawable.grey1, 1, false, false),
        Dice(1, R.drawable.grey2, 2, false, false),
        Dice(2, R.drawable.grey3, 3, false, false),
        Dice(3, R.drawable.grey4, 4, false, false),
        Dice(4, R.drawable.grey5, 5, false, false),
        Dice(5, R.drawable.grey6, 6, false, false),
    )
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        diceList = parcel.createTypedArrayList(Dice.CREATOR) ?: listOf()
    }

    fun throwDice() {
        diceList.forEach {dice ->
            dice.isRolled = true
            if(!dice.isSelected) {
                val randVal = Random.nextInt(1, 7)
                dice.value = randVal
            }
        }
    }

    fun updateDrawables() {
        diceList.forEach { dice ->
            when {
                !dice.isRolled -> setDrawColor(dice, DiceColor.GREY)
                !dice.isSelected -> setDrawColor(dice, DiceColor.WHITE)
                else -> setDrawColor(dice, DiceColor.RED)
            }
        }
    }

    fun disableSelected() {
        diceList.forEach { dice ->
            if(dice.isSelected) {
                dice.isSelected = false
                dice.isRolled = false
            }
        }
    }

    fun unSelectAllDice() {
        diceList.forEach { dice ->
            dice.isSelected = false
        }
    }

    fun toggleSelected(selectedDice: Dice) {
        if(selectedDice.isSelected) {
            diceList.forEach { dice ->
                if(dice.id == selectedDice.id) {
                    dice.isSelected = false
                }
            }
        }
        else if(selectedDice.isRolled) {
            diceList.forEach { dice ->
                if(dice.id == selectedDice.id) {
                    dice.isSelected = true
                }
            }
        }
    }

    fun disableDice() {
        diceList.forEach {dice ->
            dice.isRolled = false
        }
    }

    private fun setDrawColor(dice: Dice, color: DiceColor) {
        dice.drawableResId = when (color) {
            DiceColor.RED -> {
                when (dice.value) {
                    1 -> R.drawable.red1
                    2 -> R.drawable.red2
                    3 -> R.drawable.red3
                    4 -> R.drawable.red4
                    5 -> R.drawable.red5
                    6 -> R.drawable.red6
                    else -> throw IllegalArgumentException("Invalid dice value: ${dice.value}")
                }
            }
            DiceColor.GREY -> {
                when (dice.value) {
                    1 -> R.drawable.grey1
                    2 -> R.drawable.grey2
                    3 -> R.drawable.grey3
                    4 -> R.drawable.grey4
                    5 -> R.drawable.grey5
                    6 -> R.drawable.grey6
                    else -> throw IllegalArgumentException("Invalid dice value: ${dice.value}")
                }
            }
            DiceColor.WHITE -> {
                when (dice.value) {
                    1 -> R.drawable.white1
                    2 -> R.drawable.white2
                    3 -> R.drawable.white3
                    4 -> R.drawable.white4
                    5 -> R.drawable.white5
                    6 -> R.drawable.white6
                    else -> throw IllegalArgumentException("Invalid dice value: ${dice.value}")
                }
            }
        }
    }

    fun reset() {
        diceList = listOf(
            Dice(0, R.drawable.grey1, 1, false, false),
            Dice(1, R.drawable.grey2, 2, false, false),
            Dice(2, R.drawable.grey3, 3, false, false),
            Dice(3, R.drawable.grey4, 4, false, false),
            Dice(4, R.drawable.grey5, 5, false, false),
            Dice(5, R.drawable.grey6, 6, false, false),
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(diceList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SetOfDice> {
        override fun createFromParcel(parcel: Parcel): SetOfDice {
            return SetOfDice(parcel)
        }

        override fun newArray(size: Int): Array<SetOfDice?> {
            return arrayOfNulls(size)
        }
    }


}