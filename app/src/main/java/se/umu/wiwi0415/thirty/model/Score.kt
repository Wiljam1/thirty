package se.umu.wiwi0415.thirty.model

import android.os.Parcel
import android.os.Parcelable
import java.lang.StringBuilder
import java.util.LinkedList

data class Score (
    var totalScore: Int,
    var lastAddedScore: Int,
    var lastRoundScore: Int,
    private val scoreSteps: Array<LinkedList<String>> = Array(10) { LinkedList() }
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
    ) {
        val size = parcel.readInt()
        for (i in 0 until size) {
            val sublist = LinkedList<String>()
            parcel.readStringList(sublist)
            scoreSteps[i] = sublist
        }
    }

    fun reset() {
        totalScore = 0
        lastAddedScore = 0
        lastRoundScore = 0
        scoreSteps.forEach { sublist -> sublist.clear() }
    }

    fun countScore(setOfDice: SetOfDice, mode: String) {
        val score = when (mode) {
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
        val listOfSteps: MutableList<Int> = mutableListOf()
        setOfDice.diceList.forEach { dice ->
            if (dice.value <= 3 && dice.isRolled) {
                countedScore += dice.value
                listOfSteps.add(dice.value)
                dice.isRolled = false
                dice.isSelected = false
            }
        }
        val resultStep = "${listOfSteps.joinToString(" + ")} = $countedScore"
        scoreSteps[0].add(resultStep)
        return countedScore
    }

    private fun countNumber(setOfDice: SetOfDice, targetSum: Int): Int {
        var countedScore = 0
        val listOfSteps: MutableList<Int> = mutableListOf()
        setOfDice.diceList.forEach { dice ->
            if (dice.isSelected) {
                countedScore += dice.value
                listOfSteps.add(dice.value)
            }
        }
        // Calculate index for the sublist
        val sublistIndex = targetSum - 3
        return if (countedScore == targetSum && sublistIndex in 1 until scoreSteps.size) {
            setOfDice.disableSelected()
            val resultStep = "${listOfSteps.joinToString(" + ")} = $countedScore"
            scoreSteps[sublistIndex].add(resultStep)
            countedScore
        } else {
            0
        }
    }

    fun displayScoreboard(): String {
        val builder = StringBuilder()
        builder.append("Scoreboard:\n")
        scoreSteps.forEachIndexed { index, sublist ->
            if (sublist.isNotEmpty()) {
                val mode = when (index) {
                    0 -> "LOW"
                    1 -> "FOUR"
                    2 -> "FIVE"
                    3 -> "SIX"
                    4 -> "SEVEN"
                    5 -> "EIGHT"
                    6 -> "NINE"
                    7 -> "TEN"
                    8 -> "ELEVEN"
                    9 -> "TWELVE"
                    else -> throw IllegalArgumentException("Invalid index: $index")
                }
                builder.append("$mode: \n")
                sublist.forEach { s: String ->
                    builder.append(s)
                    builder.append("\n")
                }
            }
        }
        return builder.toString()
    }

    private fun displayLastScoreboardEntry(mode: String): String {
        val sublistIndex = when (mode) {
            "LOW" -> 0
            "FOUR" -> 1
            "FIVE" -> 2
            "SIX" -> 3
            "SEVEN" -> 4
            "EIGHT" -> 5
            "NINE" -> 6
            "TEN" -> 7
            "ELEVEN" -> 8
            "TWELVE" -> 9
            else -> throw IllegalArgumentException("Invalid mode: $mode")
        }
        val sublist = scoreSteps[sublistIndex]
        if (sublist.isNotEmpty()) {
            return sublist.last()
        }
        return " "
    }

    fun displayScoreRoll(): String {
        return "Last round score: $lastRoundScore; Total score: $totalScore"
    }

    fun displayScoreCount(mode: String): String {
        return "Last added: ${displayLastScoreboardEntry(mode)}; Total score: $totalScore"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(totalScore)
        parcel.writeInt(lastAddedScore)
        parcel.writeInt(lastRoundScore)
        parcel.writeInt(scoreSteps.size)
        scoreSteps.forEach { sublist ->
            parcel.writeStringList(sublist)
        }
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