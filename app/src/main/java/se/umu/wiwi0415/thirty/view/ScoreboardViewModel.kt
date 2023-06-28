package se.umu.wiwi0415.thirty.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.wiwi0415.thirty.model.Score


class ScoreboardViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var scoreModel: Score
        get() = savedStateHandle[SCORE_MODEL_KEY] ?: Score(0, 0,0)
        set(value) = savedStateHandle.set(SCORE_MODEL_KEY, value)

}