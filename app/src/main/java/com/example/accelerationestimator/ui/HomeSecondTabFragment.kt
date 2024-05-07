package com.example.accelerationestimator.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.io.File


class HomeSecondTabFragment : Fragment() {
    private val rpmInputs = ArrayList<TextInputEditText>()
    private val torqueInputs = ArrayList<TextInputEditText>()
    val gearRatioInputs: ArrayList<TextInputEditText> = ArrayList()
    var rpmtTorqueViews: ArrayList<View> = ArrayList()
    private var rowCount = 0
    private var suppress_listener = false
    private lateinit var RpmTorqueLayout: LinearLayout
    private var rpmInputString: ArrayList<String> = ArrayList()
    private var torqueInputString: ArrayList<String> = ArrayList()


    // stops double calling when changing directly the value of a spinner inside the listener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_second_tab, container, false)

        //Modal dialog handling
        val imageBtn_idle_rpm = view.findViewById<ImageButton>(R.id.img_btn_idle_rpm)
        val imageBtn_redline_rpm= view.findViewById<ImageButton>(R.id.img_btn_redline_rpm)
        val imageBtn_off_clutch_rpm= view.findViewById<ImageButton>(R.id.img_btn_off_clutch_rpm)
        val imageBtn_gas_starting_levels= view.findViewById<ImageButton>(R.id.img_btn_gas_starting_level)
        val imageBtn_rpm_torque_entries= view.findViewById<ImageButton>(R.id.img_btn_rpm_torque_entries)
        val imageBtn_drivetrain_layout= view.findViewById<ImageButton>(R.id.img_btn_drivetran_layout)
        val imageBtn_drivetrain_loss= view.findViewById<ImageButton>(R.id.img_btn_drivetran_loss)
        val imageBtn_drivetrain_shifting_time= view.findViewById<ImageButton>(R.id.img_btn_drivetrain_shifting_time)
        val imageBtn_drivetrain_final_drive= view.findViewById<ImageButton>(R.id.img_btn_drivetrain_final_drive)
        val imageBtn_number_of_gears= view.findViewById<ImageButton>(R.id.img_btn_number_of_gears)

        imageBtn_idle_rpm.setOnClickListener { showCustomDialog(R.string.power_train_idle_rpm_description) }
        imageBtn_redline_rpm.setOnClickListener { showCustomDialog(R.string.power_train_redline_rpm_description) }
        imageBtn_off_clutch_rpm.setOnClickListener { showCustomDialog(R.string.off_clutch_rpm_description) }
        imageBtn_gas_starting_levels.setOnClickListener { showCustomDialog(R.string.gas_starting_level_description) }
        imageBtn_rpm_torque_entries.setOnClickListener { showCustomDialog(R.string.rpm_torque_entries_description) }
        imageBtn_drivetrain_layout.setOnClickListener { showCustomDialog(R.string.drivetrain_layout_description) }
        imageBtn_drivetrain_loss.setOnClickListener { showCustomDialog(R.string.drivetrain_loss_description) }
        imageBtn_drivetrain_shifting_time.setOnClickListener { showCustomDialog(R.string.drivetrain_shifting_time_description) }
        imageBtn_drivetrain_final_drive.setOnClickListener { showCustomDialog(R.string.drivetrain_final_drive_description) }
        imageBtn_number_of_gears.setOnClickListener { showCustomDialog(R.string.number_of_gears_description) }


        val nested_scroll_rpm_torque = view.findViewById<NestedScrollView>(R.id.nested_scroll_rpm_torque_inputs)
        val addRowButton = view.findViewById<Button>(R.id.addRowButton)
        val removeRowButton = view.findViewById<Button>(R.id.removeRowButton)


        addRowButton.setOnClickListener {
            // Add new row when Add Row button is clicked
            addRPMTorqueInputs(nested_scroll_rpm_torque)
        }

        removeRowButton.setOnClickListener {
            // Remove the last row when Remove Last Entry button is clicked
            removeRPMTorqueInputs(nested_scroll_rpm_torque)
        }




        val spinner_number_of_gears: Spinner = view.findViewById(R.id.gear_number_spinner)



        val nested_gear_ratio_view = view.findViewById<NestedScrollView>(R.id.nested_scroll_gear_ratios)
        spinner_number_of_gears.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                if (suppress_listener == false) {
                    var argument_list = get_data_loading_trigger_for_listener()
                    if (argument_list[0] != null && argument_list[0] == true) {
                        if (argument_list[0] as Boolean) {
                            suppress_listener = true
                            val dir = File(
                                (argument_list[1] as? Context)?.filesDir,
                                (argument_list[2] as? String)
                            )
                            val file = File(dir, (argument_list[3] as? String))

                            var data = file.readText()
                            val json_data = JSONObject(data)


                            val total_gears =
                                (json_data["gear_number_spinner"] as? String)?.toIntOrNull() ?: 0
                            val selectedValue = total_gears - 2 + 1
                            spinner_number_of_gears.setSelection(total_gears - 2)

                            addGearRatioInput(nested_gear_ratio_view, selectedValue)

                            val gear_entries = getGearRatioElements()
                            val gearRatioEntriesObject =
                                json_data.getJSONObject("gear_ratio_entries")
                            val keys = gearRatioEntriesObject.keys()


                            var counter = 0
                            while (keys.hasNext()) {
                                val key = keys.next() as String
                                val value = gearRatioEntriesObject.getString(key)

                                val gear_entry_text = gear_entries[counter] as? TextInputEditText
                                gear_entry_text?.setText(value)

                                counter++
                            }


                            // rpm torque entries
                            val rpm_entries_json = json_data.getJSONObject("rpm_entries")
                            val sizeRpmEntriesJson = rpm_entries_json.length()
                            val sizeDifference = sizeRpmEntriesJson - rowCount
                            if (sizeDifference > 0) {
                                repeat(sizeDifference) {
                                    addRPMTorqueInputs(nested_scroll_rpm_torque)
                                }
                            } else if (sizeDifference < 0) {
                                repeat(-sizeDifference) {
                                    removeRPMTorqueInputs(nested_scroll_rpm_torque)
                                }
                            }


                            val (rpm_entries, torque_entries) = getRPMTorqueElements()
                            val torque_entries_json = json_data.getJSONObject("torque_entries")
                            val rpm_keys = rpm_entries_json.keys()
                            val torque_keys = torque_entries_json.keys()


                            var counter_rpm_torque = 0
                            while (rpm_keys.hasNext()) {
                                val rpm_keys = rpm_keys.next() as String
                                val torque_keys = torque_keys.next() as String

                                val value_rpm = rpm_entries_json.getString(rpm_keys)
                                val value_torque = torque_entries_json.getString(torque_keys)

                                val rpm_entries_text =
                                    rpm_entries[counter_rpm_torque] as? TextInputEditText
                                val torque_entries_text =
                                    torque_entries[counter_rpm_torque] as? TextInputEditText
                                rpm_entries_text?.setText(value_rpm)
                                torque_entries_text?.setText(value_torque)

                                counter_rpm_torque++
                            }


                            setTrigger(false)

                        }
                    } else {
                        val selectedValue = position + 1 // Assuming spinner position starts from 0
                        addGearRatioInput(nested_gear_ratio_view, selectedValue)
                    }


                }else{
                    suppress_listener = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected (if needed)
            }
        }

        // Input filters
        val drivetrain_loss_input_filter = view.findViewById<TextInputEditText>(R.id.drivetrain_loss_input)
        drivetrain_loss_input_filter.filters = arrayOf<InputFilter>(MinMaxFilterFloat(0f, 100f))
        val gas_start_input_filter = view.findViewById<TextInputEditText>(R.id.gas_start_level_input)
        gas_start_input_filter.filters = arrayOf<InputFilter>(MinMaxFilterFloat(1f, 100f))


        // Add 2 initial empty rows
        if (rowCount == 0) {
            addRPMTorqueInputs(nested_scroll_rpm_torque)
            addRPMTorqueInputs(nested_scroll_rpm_torque)
        }else{

            val nestedScrollContentLayout = nested_scroll_rpm_torque.findViewById<LinearLayout>(R.id.nested_scroll_content_layout)
            // View changed

            for (i in 0 until rowCount) {
                val inflater = LayoutInflater.from(requireContext())
                val templateViewOrg = inflater.inflate(R.layout.rpm_torque_input_template, null)
                val rpmInputLayout = templateViewOrg.findViewById<TextInputLayout>(R.id.rpmInputLayout)
                val torqueInputLayout = templateViewOrg.findViewById<TextInputLayout>(R.id.torqueInputLayout)

                rpmInputLayout.hint = "RPM ${i + 1}"
                torqueInputLayout.hint = "Torque ${i + 1}"

                val rpmEditText = rpmInputLayout.findViewById<TextInputEditText>(R.id.rpmEditText)
                rpmEditText.setText("55")  // Set default text for testing purposes

                val torqueEditText = torqueInputLayout.findViewById<TextInputEditText>(R.id.torqueEditText)
                torqueEditText.setText(torqueInputString[i])


                if (view?.let { getSpeedMeasureUnit(it.context) } == true) {
                    torqueInputLayout.suffixText = requireContext().getString(R.string.torque_measure_unit)

                }else{
                    torqueInputLayout.suffixText = requireContext().getString(R.string.torque_measure_unit_Imperial)
                }

                nestedScrollContentLayout.addView(templateViewOrg)
            }

        }


        return view
    }
    fun logTextInputEditTextText(view: View) {
        if (view is TextInputEditText) {
            val hint = view.hint?.toString() ?: ""
            if (hint.contains("RPM", ignoreCase = true)) {
                rpmInputString.add(view.text.toString())
            } else {
                torqueInputString.add(view.text.toString())
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val childView = view.getChildAt(i)
                logTextInputEditTextText(childView)
            }
        }
    }

    override fun onDestroyView() {

        val nested_scroll_rpm_torque = view?.findViewById<NestedScrollView>(R.id.nested_scroll_rpm_torque_inputs)
        val nestedScrollContentLayout = nested_scroll_rpm_torque?.findViewById<LinearLayout>(R.id.nested_scroll_content_layout)
        if (nestedScrollContentLayout != null) {
            logTextInputEditTextText(nestedScrollContentLayout)
        }
        super.onDestroyView()


    }

    private fun showCustomDialog(messageResId: Int) {
        val dialog = CustomDialogFragment.newInstance(messageResId)
        dialog.show(parentFragmentManager, "CustomDialog")
    }

    private fun addRPMTorqueInputs(nestedScrollView: NestedScrollView, resetView: Boolean = false, currentCount: Int = 0) {
        val inflater = LayoutInflater.from(requireContext())
        val templateView = inflater.inflate(R.layout.rpm_torque_input_template, null)

        val rpmInputLayout = templateView.findViewById<TextInputLayout>(R.id.rpmInputLayout)
        val torqueInputLayout = templateView.findViewById<TextInputLayout>(R.id.torqueInputLayout)

        if (!resetView) {
            rowCount++
            rpmInputLayout.hint = "RPM $rowCount"
            torqueInputLayout.hint = "Torque $rowCount"
        }else{
            rpmInputLayout.hint = "RPM $currentCount"
            torqueInputLayout.hint = "Torque $currentCount"
        }
        val newIdrpm = View.generateViewId()
        val newIdtorque = View.generateViewId()
        rpmInputLayout.id = newIdrpm
        torqueInputLayout.id = newIdtorque

        val rpmEditText = rpmInputLayout.findViewById<TextInputEditText>(R.id.rpmEditText)
        val torqueEditText = torqueInputLayout.findViewById<TextInputEditText>(R.id.torqueEditText)


        if (view?.let { getSpeedMeasureUnit(it.context) } == true) {
            torqueInputLayout.suffixText = requireContext().getString(R.string.torque_measure_unit)

        }else{
            torqueInputLayout.suffixText = requireContext().getString(R.string.torque_measure_unit_Imperial)
        }



        rpmInputs.add(rpmEditText)
        torqueInputs.add(torqueEditText)

        setRPMTorqueElements(rpmInputs, torqueInputs)
        rpmtTorqueViews.add(templateView)
        val nestedScrollContentLayout = nestedScrollView.findViewById<LinearLayout>(R.id.nested_scroll_content_layout)
        nestedScrollContentLayout.addView(templateView)
    }

    private fun removeRPMTorqueInputs(nestedScrollView: NestedScrollView) {
        if (rowCount > 2) {
            // Remove the last row if there are more than 2 rows present
            val nestedScrollContentLayout = nestedScrollView.findViewById<LinearLayout>(R.id.nested_scroll_content_layout)
            nestedScrollContentLayout.removeViewAt(nestedScrollContentLayout.childCount - 1)

            // Update lists and row count
            rpmInputs.removeAt(rpmInputs.size - 1)
            torqueInputs.removeAt(torqueInputs.size - 1)
            rowCount--

            rpmtTorqueViews.removeAt(rpmtTorqueViews.size - 1)
            setRPMTorqueElements(rpmInputs, torqueInputs)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun addGearRatioInput(nestedScrollView: NestedScrollView, selectedValue: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val nestedScrollContentLayout = nestedScrollView.findViewById<LinearLayout>(R.id.gear_ratio_content_layout)

        // Remove all existing layouts
        nestedScrollContentLayout.removeAllViews()
        gearRatioInputs.clear()

        for (i in 0 until selectedValue + 1) {
            val templateView = inflater.inflate(R.layout.gear_ratio_template, null)
            val gearRatioInputLayout = templateView.findViewById<TextInputLayout>(R.id.gearRatioLayout)
            gearRatioInputLayout.hint = "Gear Ratio ${i + 1}"


            val gearRatioEditText = templateView.findViewById<TextInputEditText>(R.id.gearRatioEditText)
            gearRatioEditText.id = resources.getIdentifier("gear_ratio_${i+1}", "id", "com.example.accelerationestimator")


            nestedScrollContentLayout.addView(templateView)
            gearRatioInputs.add(gearRatioEditText)
        }
        setGearRatiosElements(gearRatioInputs)

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
