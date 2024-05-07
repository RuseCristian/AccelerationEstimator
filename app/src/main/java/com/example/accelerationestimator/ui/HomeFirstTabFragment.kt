package com.example.accelerationestimator.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class HomeFirstTabFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_first_tab, container, false)


        val imageBtn_car_mass_distribution = view.findViewById<ImageButton>(R.id.img_btn_car_mass_distribution)
        val car_mass_layout = view.findViewById<TextInputLayout>(R.id.car_mass_box)


        if (getSpeedMeasureUnit(view.context) == true) {
            car_mass_layout.suffixText = requireContext().getString(R.string.mass_measure_unit)

        }else{
            car_mass_layout.suffixText = requireContext().getString(R.string.mass_measure_imperial)
        }


        // Set click listeners for each image button
        imageBtn_car_mass_distribution.setOnClickListener { showCustomDialog(R.string.car_mass_distribution_description) }



        val car_mass_distribution_input_filter = view.findViewById<TextInputEditText>(R.id.car_mass_distribution_input)
        car_mass_distribution_input_filter.filters = arrayOf<InputFilter>(MinMaxFilterFloat(1f, 100f))


        return view
    }

    private fun showCustomDialog(messageResId: Int) {
        val dialog = CustomDialogFragment.newInstance(messageResId)
        dialog.show(parentFragmentManager, "CustomDialog")
    }


    inner class MinMaxFilterFloat : InputFilter {
        private var floatMin: Float = 0f
        private var floatMax: Float = 1f

        // Initialized
        constructor(minValue: Float, maxValue: Float) {
            this.floatMin = minValue
            this.floatMax = maxValue
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = (dest.toString() + source.toString()).toFloat()
                if (isInRange(floatMin, floatMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(min: Float, max: Float, value: Float): Boolean {
            return value in min..max
        }
    }


}
