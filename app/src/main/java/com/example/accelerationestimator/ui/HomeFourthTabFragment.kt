package com.example.accelerationestimator.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.io.File


class HomeFourthTabFragment : Fragment() {
    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_fourth_tab, container, false)



        // Modal Dialog handling
        val imageBtn_aerodynamics_coefficient_of_drag = view.findViewById<ImageButton>(R.id.img_btn_aero_coefficient_of_drag)
        val imageBtn_aerodynamics_frontal_area= view.findViewById<ImageButton>(R.id.img_btn_aero_frontal_area)
        val imageBtn_aerodynamics_air_density= view.findViewById<ImageButton>(R.id.img_btn_air_density)
        val imageBtn_aerodynamics_downforce_switch= view.findViewById<ImageButton>(R.id.image_btn_downforce_calc)
        val imageBtn_aerodynamics_negative_lift_coeff= view.findViewById<ImageButton>(R.id.img_btn_negative_life_coefficient)
        val imageBtn_aerodynamics_downforce_total_area =  view.findViewById<ImageButton>(R.id.img_btn_downforce_total_area)
        val imageBtn_aerodynamics_downforce_distribution =  view.findViewById<ImageButton>(R.id.img_btn_downforce_distribution)
        imageBtn_aerodynamics_coefficient_of_drag.setOnClickListener { showCustomDialog(R.string.aerodynamics_coefficient_of_drag_description) }
        imageBtn_aerodynamics_frontal_area.setOnClickListener { showCustomDialog(R.string.aerodynamics_frontal_area_description) }
        imageBtn_aerodynamics_air_density.setOnClickListener { showCustomDialog(R.string.aerodynamics_air_density_description) }
        imageBtn_aerodynamics_downforce_switch.setOnClickListener { showCustomDialog(R.string.aerodynamics_downforce_extra_calc_description) }
        imageBtn_aerodynamics_negative_lift_coeff.setOnClickListener { showCustomDialog(R.string.negative_lift_description) }
        imageBtn_aerodynamics_downforce_total_area.setOnClickListener { showCustomDialog(R.string.downforce_total_area_description) }
        imageBtn_aerodynamics_downforce_distribution.setOnClickListener { showCustomDialog(R.string.downforce_distribution_description) }






        // Extra downforce calculation
        val downforceSwitch = view.findViewById<Switch>(R.id.aero_downforce_calc_switch)
        val negative_lift_text_edit = view.findViewById<View>(R.id.negative_lift_coefficient_box)
        val downforce_total_area_text_edit = view.findViewById<View>(R.id.downforce_total_area_box)
        val downforce_distribution_text_edit = view.findViewById<View>(R.id.downforce_distribution_box)
        val negative_lift_img_btn = view.findViewById<View>(R.id.img_btn_negative_life_coefficient)
        val downforce_total_area_img_btn = view.findViewById<View>(R.id.img_btn_downforce_total_area)
        val downforce_distribution_img_btn = view.findViewById<View>(R.id.img_btn_downforce_distribution)



        downforceSwitch.isChecked = true
        downforceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                negative_lift_text_edit.visibility = View.VISIBLE
                downforce_total_area_text_edit.visibility = View.VISIBLE
                downforce_distribution_text_edit.visibility = View.VISIBLE
                negative_lift_img_btn.visibility = View.VISIBLE
                downforce_total_area_img_btn.visibility = View.VISIBLE
                downforce_distribution_img_btn.visibility = View.VISIBLE
                var argument_list = get_data_loading_trigger_for_listener()
                if (argument_list[0] != null && argument_list[0] == true) {
                    if (argument_list[0] as Boolean) {
                        val dir = File(
                            (argument_list[1] as? Context)?.filesDir,
                            (argument_list[2] as? String)
                        )
                        val file = File(dir, (argument_list[3] as? String))

                        var data = file.readText()
                        val json_data = JSONObject(data)


                        (json_data["negative_lift_coefficient_input"] as? String)?.let {
                            (view.findViewById<View>(R.id.negative_lift_coefficient_input) as? TextInputEditText)?.setText(it)
                        }
                        (json_data["downforce_total_area_input"] as? String)?.let {
                            (view.findViewById<View>(R.id.downforce_total_area_input) as? TextInputEditText)?.setText(it)
                        }
                        (json_data["downforce_distribution_input"] as? String)?.let {
                            (view.findViewById<View>(R.id.downforce_distribution_input) as? TextInputEditText)?.setText(it)
                        }

                    }
                }



            } else {
                negative_lift_text_edit.visibility = View.GONE
                downforce_total_area_text_edit.visibility = View.GONE
                downforce_distribution_text_edit.visibility = View.GONE
                negative_lift_img_btn.visibility = View.GONE
                downforce_total_area_img_btn.visibility = View.GONE
                downforce_distribution_img_btn.visibility = View.GONE
            }
        }
        downforceSwitch.isChecked = false

        val downforce_distribution_input_filter = view.findViewById<TextInputEditText>(R.id.downforce_distribution_input)
        downforce_distribution_input_filter.filters = arrayOf<InputFilter>(MinMaxFilterFloat(0f, 100f))

        val frontal_area_box = view.findViewById<TextInputLayout>(R.id.aero_frontal_area_box)
        val air_density_box = view.findViewById<TextInputLayout>(R.id.aero_air_density_box)
        val downforce_area_box = view.findViewById<TextInputLayout>(R.id.downforce_total_area_box)
        if (view?.let { getSpeedMeasureUnit(it.context) } == true) {
            frontal_area_box.suffixText = requireContext().getString(R.string.area_measure_unit)
            air_density_box.suffixText = requireContext().getString(R.string.density_measure_unit)
            downforce_area_box.suffixText = requireContext().getString(R.string.area_measure_unit)

        }else{
            frontal_area_box.suffixText = requireContext().getString(R.string.area_measure_unit_imperial)
            air_density_box.suffixText = requireContext().getString(R.string.density_measure_unit_imperial)
            downforce_area_box.suffixText = requireContext().getString(R.string.area_measure_unit_imperial)
        }


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