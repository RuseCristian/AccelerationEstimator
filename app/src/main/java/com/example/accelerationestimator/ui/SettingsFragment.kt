package com.example.accelerationestimator.ui;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        val radioGroup = rootView.findViewById<RadioGroup>(R.id.measurement_units_radio_group)
        val radioImperial = rootView.findViewById<RadioButton>(R.id.radio_imperial_units)
        val radioMetric = rootView.findViewById<RadioButton>(R.id.radio_metric_units)

        var measurementType = getSpeedMeasureUnit(rootView.context)

        radioMetric.isChecked = measurementType
        radioImperial.isChecked = !measurementType

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isMetricSelected = checkedId == R.id.radio_metric_units
            setSpeedMeasureUnit(rootView.context, isMetricSelected)
        }

        return rootView
    }
}
