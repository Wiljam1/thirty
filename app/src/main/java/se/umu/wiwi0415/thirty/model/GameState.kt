package se.umu.wiwi0415.thirty.model

import android.os.Parcel
import android.os.Parcelable

data class GameState(var throws: Int = 0, var round: Int = 1
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    fun reset() {
        throws = 0
        round = 1
    }

    fun displayGameState(): String {
        return if(round < 11)
            "Round: $round    Throws: $throws"
        else
            "GAME IS DONE"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(throws)
        parcel.writeInt(round)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameState> {
        override fun createFromParcel(parcel: Parcel): GameState {
            return GameState(parcel)
        }

        override fun newArray(size: Int): Array<GameState?> {
            return arrayOfNulls(size)
        }
    }
}
