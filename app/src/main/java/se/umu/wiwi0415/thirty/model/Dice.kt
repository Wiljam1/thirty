package se.umu.wiwi0415.thirty.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes

data class Dice (
    val id: Int,
    @DrawableRes var drawableResId: Int,
    var value: Int,
    var isSelected: Boolean,
    var isRolled: Boolean
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(drawableResId)
        parcel.writeInt(value)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isRolled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }
}