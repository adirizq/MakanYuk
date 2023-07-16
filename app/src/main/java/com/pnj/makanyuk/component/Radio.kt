package com.pnj.makanyuk.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.pnj.makanyuk.R

class Radio(context: Context?, attrs: AttributeSet?) : AppCompatRadioButton(context, attrs) {

    override fun setChecked(t: Boolean) {
        if (t) {
            setBackgroundResource(R.drawable.checkbox_selected)
        } else {
            setBackgroundResource(R.drawable.checkbox_normal)
        }
        super.setChecked(t)
    }
}