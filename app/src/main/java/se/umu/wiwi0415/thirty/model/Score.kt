package se.umu.wiwi0415.thirty.model

import android.os.Parcel
import android.os.Parcelable

data class Score (
    var totalScore: Int,
    var lastAddedScore: Int,
    var lastRoundScore: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun reset() {
        totalScore = 0
        lastAddedScore = 0
        lastRoundScore = 0
    }

    fun countScore(setOfDice: SetOfDice, mode: String) {
        val score = when(mode) {
            "LOW" -> countLow(setOfDice)
            "FOUR" -> countNumber(setOfDice, 4)
            "FIVE" -> countNumber(setOfDice, 5)
            "SIX" -> countNumber(setOfDice, 6)
            "SEVEN" -> countNumber(setOfDice, 7)
            "EIGHT" -> countNumber(setOfDice, 8)
            "NINE" -> countNumber(setOfDice, 9)
            "TEN" -> countNumber(setOfDice, 10)
            "ELEVEN" -> countNumber(setOfDice, 11)
            "TWELVE" -> countNumber(setOfDice, 12)
            else -> throw IllegalArgumentException("Invalid mode: $mode")
        }

        totalScore += score
        lastRoundScore = score
    }

    private fun countLow(setOfDice: SetOfDice): Int {
        var countedScore = 0
        setOfDice.diceList.forEach { dice ->
            if(dice.value <= 3 && dice.isRolled) {
                countedScore += dice.value
                dice.isRolled = false
            }
        }
        return countedScore
    }

    private fun countNumber(setOfDice: SetOfDice, targetSum: Int): Int {
        var countedValue = 0
        setOfDice.diceList.forEach { dice ->
            if(dice.isSelected)
                countedValue += dice.value
        }
        return if(countedValue == targetSum) {
            setOfDice.disableSelected()
            countedValue
        } else {
            0
        }
    }

    fun displayScoreRoll(): String {
        return "Last round score: $lastRoundScore ; Total score: $totalScore"
    }

    fun displayScoreCount(): String {
        return "Last added score: $lastAddedScore ; Total score: $totalScore"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(totalScore)
        parcel.writeInt(lastAddedScore)
        parcel.writeInt(lastRoundScore)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Score> {
        override fun createFromParcel(parcel: Parcel): Score {
            return Score(parcel)
        }

        override fun newArray(size: Int): Array<Score?> {
            return arrayOfNulls(size)
        }
    }
}