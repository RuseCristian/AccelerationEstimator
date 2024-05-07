package com.example.accelerationestimator.ui


import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.accelerationestimator.R
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject


class HomeFifthTabFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_fifth_tab, container, false)

        var show_result_btn = view.findViewById<View>(R.id.show_results_btn)



        var save_data_set_btn = view.findViewById<View>(R.id.save_data_set_btn)
        var load_data_set_btn = view.findViewById<View>(R.id.load_data_set_btn)
        var delete_data_set_btn = view.findViewById<View>(R.id.delete_data_set_btn)

        val graphLabel = view.findViewById<View>(R.id.graph_label)
        val graphSpinner = view.findViewById<View>(R.id.graph_spinner) as? Spinner
        val graphButton = view.findViewById<Button>(R.id.show_graph_btn)

        save_data_set_btn.setOnClickListener{
            if(check_data(true)) {
                val data = jsonifyData()
                val jsonObject = JSONObject(data)
                val carName = jsonObject.optString("car_name_input")
                writeFileOnInternalStorage(view.context, carName, data)
                Toast.makeText(context, "Data Set '$carName' successfully saved.", Toast.LENGTH_SHORT).show()
            }
        }

        delete_data_set_btn.setOnClickListener{
            showFileSelectionDialog(view.context, "mydir", "delete") { fileName ->
                if (fileName.isNotEmpty()) {
                    deleteFileFromInternalStorage(view.context, fileName)
                }
            }
        }

        load_data_set_btn.setOnClickListener{
            showFileSelectionDialog(view.context, "mydir", "load") { fileName ->
                if (fileName.isNotEmpty()) {
                    loadFileContent(view.context, "mydir", fileName)
                    Toast.makeText(context, "Data Set successfully loaded.", Toast.LENGTH_SHORT).show()
                    graphLabel.visibility = View.GONE
                    if (graphSpinner != null) {
                        graphSpinner.visibility = View.GONE
                    }
                    graphButton.visibility = View.GONE
                }
            }
        }


        show_result_btn.setOnClickListener{
            if(check_data()) {
                estimate_acceleration()

                graphLabel.visibility = View.VISIBLE
                if (graphSpinner != null) {
                    graphSpinner.visibility = View.VISIBLE
                }
                graphButton.visibility = View.VISIBLE
            } else{
                graphLabel.visibility = View.GONE
                if (graphSpinner != null) {
                    graphSpinner.visibility = View.GONE
                }
                graphButton.visibility = View.GONE
            }
        }





        graphButton.setOnClickListener{
            val selectedItemPosition = graphSpinner?.selectedItemPosition
            val selectedItemId =
                selectedItemPosition?.let { graphSpinner.getItemIdAtPosition(it) }
            if (selectedItemId != null) {
                setGraphTypes(selectedItemId.toInt())
            }

            startActivity(Intent(view?.context, Graphs::class.java))

        }


        val initial_speed_box = view.findViewById<TextInputLayout>(R.id.initial_speed_box)
        val final_speed_box = view.findViewById<TextInputLayout>(R.id.final_speed_box)
        if (view?.let { getSpeedMeasureUnit(it.context) } == true) {
            initial_speed_box.suffixText = requireContext().getString(R.string.speed_measure_unit)
            final_speed_box.suffixText = requireContext().getString(R.string.speed_measure_unit)

        }else{
            initial_speed_box.suffixText = requireContext().getString(R.string.speed_measure_unit_imperial)
            final_speed_box.suffixText = requireContext().getString(R.string.speed_measure_unit_imperial)
        }

        return view
    }


}