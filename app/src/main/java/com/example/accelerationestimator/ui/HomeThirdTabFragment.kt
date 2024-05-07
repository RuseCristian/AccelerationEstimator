package com.example.accelerationestimator.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R
import com.google.android.material.textfield.TextInputEditText


class HomeThirdTabFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_home_third_tab, container, false)


        val imageBtn_tires_width = view.findViewById<ImageButton>(R.id.img_btn_tires_width)
        val imageBtn_tires_aspect_ratio= view.findViewById<ImageButton>(R.id.img_btn_tires_aspect_ratio)
        val imageBtn_tires_wheel_diameter= view.findViewById<ImageButton>(R.id.img_btn_tires_wheel_diameter)
        val imageBtn_tires_friction_coeff= view.findViewById<ImageButton>(R.id.img_btn_tires_friction_coeff)
        val imageBtn_tires_rolling_res_coeff =  view.findViewById<ImageButton>(R.id.img_btn_tires_rolling_coeff_input)


        imageBtn_tires_width.setOnClickListener { showCustomDialog(R.string.tires_width_description_description) }
        imageBtn_tires_aspect_ratio.setOnClickListener { showCustomDialog(R.string.tires_aspect_ratio_description) }
        imageBtn_tires_wheel_diameter.setOnClickListener { showCustomDialog(R.string.tires_wheel_diameter_description) }
        imageBtn_tires_friction_coeff.setOnClickListener { showCustomDialog(R.string.tires_friction_coefficient_description) }
        imageBtn_tires_rolling_res_coeff.setOnClickListener { showCustomDialog(R.string.tires_rolling_res_coefficient_description) }

        val tire_aspect_ratio_input_filter = view.findViewById<TextInputEditText>(R.id.tires_aspect_ratio_input)
        tire_aspect_ratio_input_filter.filters = arrayOf<InputFilter>(MinMaxFilterFloat(0f, 100f))



        // Inflate the layout for this fragment
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