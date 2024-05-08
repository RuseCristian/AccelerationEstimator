package com.example.accelerationestimator.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.accelerationestimator.R
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private var storedRpmInputLayouts = ArrayList<TextInputEditText>()
private var storedTorqueInputLayouts = ArrayList<TextInputEditText>()
private var storedGearRatioElements = ArrayList<TextInputEditText>()
private var fragmentManagerValue: FragmentManager? = null
private var data_loading_trigger_for_listener: Any? = null
private var context: Any? = null
private var directoryPath: Any? = null
private var fileName: Any? = null
var gearData: ArrayList<Gear>? = null
var graphType: Int = 0

private var rpmInputsUI = ArrayList<TextInputEditText>()
private var torqueInputsUI = ArrayList<TextInputEditText>()
private var gearRatioInputsUI: ArrayList<TextInputEditText> = ArrayList()


private var settingsSpeedMeasureUnit = true
private const val settingsFolder = "settings"
private const val settingsFileName = "settings.txt"

fun setRpmInputsUI(inputs: ArrayList<TextInputEditText>) {
    rpmInputsUI = inputs
}

fun getRpmInputsUI(): ArrayList<TextInputEditText> {
    return rpmInputsUI
}

// Setter and getter for torqueInputsUI
fun setTorqueInputsUI(inputs: ArrayList<TextInputEditText>) {
    torqueInputsUI = inputs
}

fun getTorqueInputsUI(): ArrayList<TextInputEditText> {
    return torqueInputsUI
}

// Setter and getter for gearRatioInputsUI
fun setGearRatioInputsUI(inputs: ArrayList<TextInputEditText>) {
    gearRatioInputsUI = inputs
}

fun getGearRatioInputsUI(): ArrayList<TextInputEditText> {
    return gearRatioInputsUI
}

fun getAllViews(root: View): Map<Int, View> {
    val viewsMap = mutableMapOf<Int, View>()

    fun traverseView(view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val childView = view.getChildAt(i)
                traverseView(childView)
            }
        } else {
            viewsMap[view.id] = view
        }
    }

    traverseView(root)
    return viewsMap
}

fun setFragmentManager(manager: FragmentManager) {
    fragmentManagerValue = manager
}

fun setGearObjects(gearData_: ArrayList<Gear>) {
    gearData = gearData_
}

fun getGearObjects(): ArrayList<Gear>? {
    return gearData
}


fun setGraphTypes(graphType_: Any?) {
    graphType = graphType_ as Int
}

fun getGraphTypes(): Int {
    return graphType
}


fun setRPMTorqueElements(
    rpmInputLayouts: ArrayList<TextInputEditText>, torqueInputLayouts: ArrayList<TextInputEditText>
) {
    // Store the ArrayLists in global variables
    storedRpmInputLayouts = rpmInputLayouts
    storedTorqueInputLayouts = torqueInputLayouts
}

// Function to retrieve the ArrayLists
fun getRPMTorqueElements(): Pair<ArrayList<TextInputEditText>, ArrayList<TextInputEditText>> {
    // Return the stored ArrayLists
    return Pair(storedRpmInputLayouts, storedTorqueInputLayouts)
}

fun setGearRatiosElements(
    gearRatioElements: ArrayList<TextInputEditText>,
) {
    // Store the ArrayLists in global variables
    storedGearRatioElements = gearRatioElements
}

// Function to retrieve the ArrayLists
fun getGearRatioElements(): ArrayList<TextInputEditText> {
    // Return the stored ArrayLists
    return storedGearRatioElements
}

fun getChildFragment(fragmentManager: FragmentManager, position: Int): Fragment? {
    return fragmentManager.findFragmentByTag("f$position")
}

fun get_data_loading_trigger_for_listener(): List<Any?> {

    return listOf(data_loading_trigger_for_listener, context, directoryPath, fileName)
}

fun set_data_loading_trigger_for_listener(
    trigger: Any?, context_: Any?, directoryPath_: Any?, fileName_: Any?
) {
    data_loading_trigger_for_listener = trigger
    context = context_
    directoryPath = directoryPath_
    fileName = fileName_
}

@SuppressLint("UseSwitchCompatOrMaterialCode")
fun check_data(save_data: Boolean = false): Boolean {

    val data = getTabsData(save_data)

    var counter = if (save_data == false) {
        4 // Counter value for save_data == false
    } else {
        3 // Counter value for save_data == true
    }

    for (counter in 0..counter) {
        if (!anyEmpty(data[counter])) return false

    }


    val (rpmInputs, torqueInputs) = getRPMTorqueElements()
    for (i in 0 until rpmInputs.size - 1) {
        val currentRpmText = rpmInputs[i].text.toString()
        val nextRpmText = rpmInputs[i + 1].text.toString()

        val currentRpm = currentRpmText.toBigIntegerOrNull()
        val nextRpm = nextRpmText.toBigIntegerOrNull()


        if (currentRpm != null && nextRpm != null) {
            if (currentRpm >= nextRpm) {
                AlertDialog.Builder(rpmInputs[i].context)
                    .setMessage("RPM entry ${i + 2} is is less than or equal to RPM entry ${i + 1} in Power Train Tab, ${nextRpm} RPM and ${currentRpm} RPM, respectively. Please correct this because the rpm entries must be in ascending order. Example 1500 -> 2000 -> 2500 etc.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
                return false
            }
        } else {
            var rpm_id = 0
            if (nextRpm == null) {
                rpm_id = i + 2
            }
            if (currentRpm == null) {
                rpm_id = i + 1
            }

            AlertDialog.Builder(rpmInputs[i].context).setMessage("RPM entry ${rpm_id} is not yet configured.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
            return false
        }
    }

    for (i in 0 until torqueInputs.size) {
        val torqueText = torqueInputs[i].text.toString().toBigIntegerOrNull()
        if (torqueText == null) {
            AlertDialog.Builder(torqueInputs[i].context).setMessage("Torque entry ${i + 1} is not yet configured.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
            return false
        }
    }


    var gearRatios = getGearRatioElements()
    for (i in 0 until gearRatios.size) {
        val gearRatio = gearRatios[i].text.toString().toDoubleOrNull()
        if (gearRatio == null) {
            AlertDialog.Builder(gearRatios[i].context).setMessage("Gear Ratio ${i + 1} is not yet configured.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
            return false
        }
    }


    val idleRPMelement = (data[1][R.id.idle_rpm_input] as? TextInputEditText)?.text?.toString()
    val offclutchRPMelement = (data[1][R.id.off_clutch_rpm_input] as? TextInputEditText)?.text?.toString()
    val redlineRPMelement = (data[1][R.id.redline_rpm_input] as? TextInputEditText)?.text?.toString()


    if (idleRPMelement != null && offclutchRPMelement != null && redlineRPMelement != null) {
        val idleRpmValue = idleRPMelement.toIntOrNull()
        val offClutchRpmValue = offclutchRPMelement.toIntOrNull()
        val redlineRpmValue = redlineRPMelement.toIntOrNull()

        if (idleRpmValue != null && offClutchRpmValue != null && redlineRpmValue != null) {
            if (!(idleRpmValue >= 500 && idleRpmValue < offClutchRpmValue && offClutchRpmValue < redlineRpmValue)) {
                AlertDialog.Builder(data[1][R.id.off_clutch_rpm_input]?.context)
                    .setMessage("Condition in Power Train Tab is not met:  500 <= Idle RPM < Off Clutch RPM <= Redline RPM")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
                return false
            }
        } else {
        }

    }

    if (idleRPMelement != null) {
        if (idleRPMelement.toIntOrNull() != rpmInputs[0].text.toString().toIntOrNull()) {
            AlertDialog.Builder(data[1][R.id.off_clutch_rpm_input]?.context)
                .setMessage("Idle RPM and first rpm entry are not equal.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()

            return false
        }

    }

    if (redlineRPMelement != null) {
        if (redlineRPMelement.toIntOrNull() != rpmInputs.lastOrNull()?.text.toString().toIntOrNull()) {
            AlertDialog.Builder(data[1][R.id.off_clutch_rpm_input]?.context)
                .setMessage("Redline RPM and last rpm entry are not equal.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()

            return false
        }

    }

    if (!save_data) {
        val initialSpeed = (data[4][R.id.initial_speed_input] as? TextInputEditText)?.text.toString().toIntOrNull() ?: 0
        val finalSpeed = (data[4][R.id.final_speed_input] as? TextInputEditText)?.text.toString().toIntOrNull() ?: 0
        if (initialSpeed >= finalSpeed) {
            AlertDialog.Builder(data[1][R.id.off_clutch_rpm_input]?.context)
                .setMessage("Initial speed can't be higher or equal to final speed.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()

            return false
        }
    }

    return true

}

fun anyEmpty(tab: MutableMap<Int, View?>): Boolean {
    for ((id, actualElement) in tab) {
        if (actualElement is TextInputEditText) {
            val text = actualElement.text?.toString()

            if (text.isNullOrEmpty()) {
                val friendlyName = actualElement.context.resources.getResourceEntryName(id)
                showDialogForResource(actualElement.context, id)
                return false
            }
        } else if (actualElement is Spinner) {
            continue
        }
    }
    return true
}

fun showDialogForResource(context: Context?, resourceId: Int) {
    val friendlyName =
        context?.resources?.getResourceEntryName(resourceId)?.split("_")?.joinToString(" ") { it.capitalize() }?.replace(" Input", "")

    val message = "$friendlyName is not yet configured."

    AlertDialog.Builder(context).setMessage(message).setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
}

@SuppressLint("UseSwitchCompatOrMaterialCode")
fun getTabsData(save_data: Boolean = false, load: Boolean = false): Array<MutableMap<Int, View?>> {
    val allViewsList = ArrayList<Map<Int, View>>()

    var counter = if (save_data == false) {
        4
    } else {
        3
    }

    for (i in 0..counter) {
        val fragment = fragmentManagerValue?.let { getChildFragment(it, i) }
        fragment?.let {
            val allViews = getAllViews(fragment.requireView())
            allViewsList.add(allViews)
        }
    }


    var tab1 = allViewsList[0]
    var tab2 = allViewsList[1]
    var tab3 = allViewsList[2]
    var tab4 = allViewsList[3]


    val tab1Fields = mutableMapOf(
        R.id.car_name_input to tab1[R.id.car_name_input],
        R.id.car_mass_input to tab1[R.id.car_mass_input],
        R.id.car_mass_distribution_input to tab1[R.id.car_mass_distribution_input]
    )

    val second_tab_fragment = fragmentManagerValue?.let { getChildFragment(it, 1) }
    var drivetrain_layout_spinner: View? = null
    var gear_number_spinner: View? = null

    if (second_tab_fragment != null) {
        drivetrain_layout_spinner = (second_tab_fragment.view?.findViewById<View>(R.id.drivetrain_layout_spinner)) as? Spinner
        gear_number_spinner = (second_tab_fragment.view?.findViewById<View>(R.id.gear_number_spinner)) as? Spinner
    }


    val tab2Fields = if (load) {
        mutableMapOf(
            R.id.idle_rpm_input to tab2[R.id.idle_rpm_input],
            R.id.redline_rpm_input to tab2[R.id.redline_rpm_input],
            R.id.off_clutch_rpm_input to tab2[R.id.off_clutch_rpm_input],
            R.id.gas_start_level_input to tab2[R.id.gas_start_level_input],
            R.id.drivetrain_loss_input to tab2[R.id.drivetrain_loss_input],
            R.id.shifting_time_input to tab2[R.id.shifting_time_input],
            R.id.final_drive_input to tab2[R.id.final_drive_input],
            R.id.drivetrain_layout_spinner to drivetrain_layout_spinner,
            R.id.gear_number_spinner to gear_number_spinner
        )
    } else {
        mutableMapOf(
            R.id.idle_rpm_input to tab2[R.id.idle_rpm_input],
            R.id.redline_rpm_input to tab2[R.id.redline_rpm_input],
            R.id.off_clutch_rpm_input to tab2[R.id.off_clutch_rpm_input],
            R.id.gas_start_level_input to tab2[R.id.gas_start_level_input],
            R.id.drivetrain_loss_input to tab2[R.id.drivetrain_loss_input],
            R.id.shifting_time_input to tab2[R.id.shifting_time_input],
            R.id.final_drive_input to tab2[R.id.final_drive_input],
            R.id.drivetrain_layout_spinner to drivetrain_layout_spinner
        )
    }


    val tab3Fields = mutableMapOf(
        R.id.tires_width_input to tab3[R.id.tires_width_input],
        R.id.tires_aspect_ratio_input to tab3[R.id.tires_aspect_ratio_input],
        R.id.tires_wheel_diameter_input to tab3[R.id.tires_wheel_diameter_input],
        R.id.tires_friction_coeff_input to tab3[R.id.tires_friction_coeff_input],
        R.id.tires_rolling_coeff_input to tab3[R.id.tires_rolling_coeff_input]
    )

    val tab4Fields = mutableMapOf<Int, View?>().apply {
        val downforce_calc = tab4[R.id.aero_downforce_calc_switch] as Switch
        if (downforce_calc.isChecked()) {
            putAll(
                mapOf(
                    R.id.aerodynamics_coefficient_of_drag_input to tab4[R.id.aerodynamics_coefficient_of_drag_input],
                    R.id.aerodynamics_frontal_area_input to tab4[R.id.aerodynamics_frontal_area_input],
                    R.id.aerodynamics_air_density_input to tab4[R.id.aerodynamics_air_density_input],
                    R.id.negative_lift_coefficient_input to tab4[R.id.negative_lift_coefficient_input],
                    R.id.downforce_total_area_input to tab4[R.id.downforce_total_area_input],
                    R.id.downforce_distribution_input to tab4[R.id.downforce_distribution_input]
                )
            )
        } else {
            putAll(
                mapOf(
                    R.id.aerodynamics_coefficient_of_drag_input to tab4[R.id.aerodynamics_coefficient_of_drag_input],
                    R.id.aerodynamics_frontal_area_input to tab4[R.id.aerodynamics_frontal_area_input],
                    R.id.aerodynamics_air_density_input to tab4[R.id.aerodynamics_air_density_input]
                )
            )
        }
    }

    val tabsFields: Array<MutableMap<Int, View?>> = if (!save_data) {
        val tab5 = allViewsList[4]
        val tab5Fields = mutableMapOf<Int, View?>(
            R.id.initial_speed_input to tab5[R.id.initial_speed_input], R.id.final_speed_input to tab5[R.id.final_speed_input]
        )
        arrayOf(
            tab1Fields, tab2Fields, tab3Fields, tab4Fields, tab5Fields
        )
    } else {
        arrayOf(
            tab1Fields, tab2Fields, tab3Fields, tab4Fields
        )
    }


    return tabsFields

}

@SuppressLint("UseSwitchCompatOrMaterialCode")
fun jsonifyData(): String {
    val jsonObject = JSONObject()
    val data = getTabsData(true, true)

    // tab 1
    jsonObject.put(
        "car_name_input", (data[0][R.id.car_name_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "car_mass_input", (data[0][R.id.car_mass_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "car_mass_distribution_input", (data[0][R.id.car_mass_distribution_input] as? TextInputEditText)?.text?.toString()
    )


    // tab 2
    jsonObject.put(
        "idle_rpm_input", (data[1][R.id.idle_rpm_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "redline_rpm_input", (data[1][R.id.redline_rpm_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "off_clutch_rpm_input", (data[1][R.id.off_clutch_rpm_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "gas_start_level_input", (data[1][R.id.gas_start_level_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "drivetrain_loss_input", (data[1][R.id.drivetrain_loss_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "shifting_time_input", (data[1][R.id.shifting_time_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "final_drive_input", (data[1][R.id.final_drive_input] as? TextInputEditText)?.text?.toString()
    )
    var rpmEntries = JSONObject()
    var torqueEntries = JSONObject()
    var gearRatioEntries = JSONObject()
    val (uneditedRpmEntries, uneditedTorqueEntries) = getRPMTorqueElements()
    val uneditedGearRatios = getGearRatioElements()

    for ((index, element) in uneditedRpmEntries.withIndex()) {
        val stringElement = (element as? TextInputEditText)?.text?.toString()
        rpmEntries.put("rpm_${index}", stringElement)
    }
    for ((index, element) in uneditedTorqueEntries.withIndex()) {
        val stringElement = (element as? TextInputEditText)?.text?.toString()
        torqueEntries.put("torque_${index}", stringElement)
    }
    for ((index, element) in uneditedGearRatios.withIndex()) {
        val stringElement = (element as? TextInputEditText)?.text?.toString()
        gearRatioEntries.put("gear_ratio_${index}", stringElement)
    }
    jsonObject.put("rpm_entries", rpmEntries)
    jsonObject.put("torque_entries", torqueEntries)
    jsonObject.put("gear_ratio_entries", gearRatioEntries)

    var drivetrain_layout = data[1][R.id.drivetrain_layout_spinner]
    var drivetrain_layout_spinner = drivetrain_layout as? Spinner
    jsonObject.put("drivetrain_layout_spinner", drivetrain_layout_spinner?.selectedItem.toString())


    var gear_number = data[1][R.id.gear_number_spinner]
    var gear_number_spinner = gear_number as? Spinner
    jsonObject.put("gear_number_spinner", gear_number_spinner?.selectedItem.toString())


    // tab 3
    jsonObject.put(
        "tires_width_input", (data[2][R.id.tires_width_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "tires_aspect_ratio_input", (data[2][R.id.tires_aspect_ratio_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "tires_wheel_diameter_input", (data[2][R.id.tires_wheel_diameter_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "tires_friction_coeff_input", (data[2][R.id.tires_friction_coeff_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "tires_rolling_coeff_input", (data[2][R.id.tires_rolling_coeff_input] as? TextInputEditText)?.text?.toString()
    )


    // tab 4
    jsonObject.put(
        "aerodynamics_coefficient_of_drag_input",
        (data[3][R.id.aerodynamics_coefficient_of_drag_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "aerodynamics_frontal_area_input", (data[3][R.id.aerodynamics_frontal_area_input] as? TextInputEditText)?.text?.toString()
    )
    jsonObject.put(
        "aerodynamics_air_density_input", (data[3][R.id.aerodynamics_air_density_input] as? TextInputEditText)?.text?.toString()
    )




    try {
        if (data[3][R.id.negative_lift_coefficient_input] != null) {
            jsonObject.put(
                "negative_lift_coefficient_input", (data[3][R.id.negative_lift_coefficient_input] as? TextInputEditText)?.text?.toString()
            )
            jsonObject.put(
                "downforce_total_area_input", (data[3][R.id.downforce_total_area_input] as? TextInputEditText)?.text?.toString()
            )
            jsonObject.put(
                "downforce_distribution_input", (data[3][R.id.downforce_distribution_input] as? TextInputEditText)?.text?.toString()
            )
        }
    } catch (e: IndexOutOfBoundsException) {
    }

    val jsonString = jsonObject.toString()
    return jsonString
}

fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
    val dir = File(mcoContext.filesDir, "mydir")
    if (!dir.exists()) {
        dir.mkdir()
    }
    try {
        val gpxfile = File(dir, sFileName)
        val writer = FileWriter(gpxfile)
        writer.append(sBody)
        writer.flush()
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteFileFromInternalStorage(context: Context, fileName: String?) {
    val dir = File(context.filesDir, "mydir")
    val file = fileName?.let { File(dir, it) }

    if (file != null) {
        if (file.exists()) {
            file.delete()
            Toast.makeText(context, "Data Set '$fileName' successfully delete.", Toast.LENGTH_SHORT).show()
        }
    }
}

fun showFileSelectionDialog(
    context: Context, directoryPath: String, options: String, onFileSelected: (fileName: String) -> Unit
) {
    val dir = File(context.filesDir, directoryPath)
    val fileList = dir.listFiles()?.map { it.name } ?: emptyList()


    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
    val title = if (options == "delete") {
        "Select a Data Set to Delete."
    } else {
        "Select a Data Set to Load."
    }
    builder.setTitle(title).setCancelable(false).setSingleChoiceItems(fileList.toTypedArray(), -1) { dialog, which -> }

    builder.setPositiveButton("Select") { dialog, which ->
        val selectedPosition = (dialog as androidx.appcompat.app.AlertDialog).listView.checkedItemPosition
        if (selectedPosition != -1) {
            val selectedFile = fileList[selectedPosition]
            onFileSelected(selectedFile)
        }
    }

    builder.setNegativeButton("Cancel") { dialog, which ->
        dialog.dismiss()
        onFileSelected("")
    }

    val dialog = builder.create()
    dialog.show()
}

fun setTrigger(value: Boolean) {
    data_loading_trigger_for_listener = value
}

fun loadFileContent(context: Context, directoryPath: String, fileName: String) {
    val dir = File(context.filesDir, directoryPath)
    val file = File(dir, fileName)

    var data = file.readText()
    val json_data = JSONObject(data)

    var data_in_app = getTabsData(true, true)


    //tab 1
    (json_data["car_name_input"] as? String)?.let {
        (data_in_app[0][R.id.car_name_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["car_mass_input"] as? String)?.let {
        (data_in_app[0][R.id.car_mass_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["car_mass_distribution_input"] as? String)?.let {
        (data_in_app[0][R.id.car_mass_distribution_input] as? TextInputEditText)?.setText(it)
    }

    //tab2
    (json_data["idle_rpm_input"] as? String)?.let {
        (data_in_app[1][R.id.idle_rpm_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["redline_rpm_input"] as? String)?.let {
        (data_in_app[1][R.id.redline_rpm_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["off_clutch_rpm_input"] as? String)?.let {
        (data_in_app[1][R.id.off_clutch_rpm_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["gas_start_level_input"] as? String)?.let {
        (data_in_app[1][R.id.gas_start_level_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["drivetrain_loss_input"] as? String)?.let {
        (data_in_app[1][R.id.drivetrain_loss_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["shifting_time_input"] as? String)?.let {
        (data_in_app[1][R.id.shifting_time_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["final_drive_input"] as? String)?.let {
        (data_in_app[1][R.id.final_drive_input] as? TextInputEditText)?.setText(it)
    }

    val selectedIndex = when (json_data["drivetrain_layout_spinner"] as? String) {
        "RWD" -> 0
        "FWD" -> 1
        "AWD" -> 2
        else -> -1 // Handle unknown value
    }

    (data_in_app[1][R.id.drivetrain_layout_spinner] as? Spinner)?.setSelection(selectedIndex)


    val total_gears = (json_data["gear_number_spinner"] as? String)?.toIntOrNull() ?: 0
    val spinner_gear_number = data_in_app[1][R.id.gear_number_spinner] as? Spinner
    spinner_gear_number?.let {
        var currentIndex = spinner_gear_number.selectedItemPosition
        currentIndex = (currentIndex + 1) % 6
        spinner_gear_number.setSelection(currentIndex)
    }


    set_data_loading_trigger_for_listener(true, context, directoryPath, fileName)

    //tab3
    (json_data["tires_width_input"] as? String)?.let {
        (data_in_app[2][R.id.tires_width_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["tires_aspect_ratio_input"] as? String)?.let {
        (data_in_app[2][R.id.tires_aspect_ratio_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["tires_wheel_diameter_input"] as? String)?.let {
        (data_in_app[2][R.id.tires_wheel_diameter_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["tires_friction_coeff_input"] as? String)?.let {
        (data_in_app[2][R.id.tires_friction_coeff_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["tires_rolling_coeff_input"] as? String)?.let {
        (data_in_app[2][R.id.tires_rolling_coeff_input] as? TextInputEditText)?.setText(it)
    }

    //tab4
    (json_data["aerodynamics_coefficient_of_drag_input"] as? String)?.let {
        (data_in_app[3][R.id.aerodynamics_coefficient_of_drag_input] as? TextInputEditText)?.setText(
            it
        )
    }
    (json_data["aerodynamics_frontal_area_input"] as? String)?.let {
        (data_in_app[3][R.id.aerodynamics_frontal_area_input] as? TextInputEditText)?.setText(it)
    }
    (json_data["aerodynamics_air_density_input"] as? String)?.let {
        (data_in_app[3][R.id.aerodynamics_air_density_input] as? TextInputEditText)?.setText(it)
    }

    var fourthtab = fragmentManagerValue?.let { getChildFragment(it, 3) }!!.view
    var aeroSwitch = fourthtab?.findViewById<View>(R.id.aero_downforce_calc_switch) as? Switch
    if (json_data.has("negative_lift_coefficient_input")) {

        if (aeroSwitch != null) {
            aeroSwitch.isChecked = true
        }

    }else{
        if (aeroSwitch != null) {
            aeroSwitch.isChecked = false
        }
    }

}

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).toInt() / factor
}

fun <T : Number> linearInterpolation(inputList: List<T>, m: Double): List<T> {
    val n = inputList.size
    val ratio = (n - 1.0) / (m - 1.0)
    val interpolatedValues = mutableListOf<T>()
    for (i in 0 until m.toInt()) {
        val x = i * ratio
        val lowerIndex = x.toInt()
        val upperIndex = min(lowerIndex + 1, n - 1)
        val lowerValue = inputList[lowerIndex].toDouble()
        val upperValue = inputList[upperIndex].toDouble()
        val interpolatedValue = lowerValue + (x - lowerIndex) * (upperValue - lowerValue)
        @Suppress("UNCHECKED_CAST") interpolatedValues.add(interpolatedValue.toFloat() as T) // Assuming T can be casted to Float
    }
    return interpolatedValues
}


class Gear(
    val gearRatio: Float,
    val accelCurve: List<Double>, // Changed to List for consistency
    val rpmCurve: List<Double>,
    val speedCurve: List<Double>, // Changed to List for consistency
    val wheelTorque: List<Double>, // Changed to List for consistency
    val airResistanceCurve: List<Double>, // Changed to List for consistency
    val downforceCurve: List<Double>, // Changed to List for consistency
    val hpCurve: List<Double>,
    val finalDrive: Float,
    val tireRadius: Double,
    val carMass: Int
) {
    var dropdownRpm: Int? = null
    var topSpeed: Double? = null // Changed to Double to match speedCurve's type
    var upshiftRpm: Int? = null

    fun addOptimumUpshift(value: Int) {
        upshiftRpm = value
        topSpeed = speedCurve[minOf(value, speedCurve.size - 1)]
    }

    fun addDropdownRpm(value: Int) {
        dropdownRpm = value
    }
}


fun searchsorted(arr: List<Double>, value: Double): Int {
    var left = 0
    var right = arr.size

    while (left < right) {
        val mid = (left + right) / 2
        if (arr[mid] < value) {
            left = mid + 1
        } else {
            right = mid
        }
    }
    return left
}

fun findAccelFromSpeed(speed: Double, speedValues: List<Double>, accelValues: List<Double>): Double {
    val rpm = searchsorted(speedValues, speed)
    return if (rpm != -1) accelValues[rpm] else 0.0
}

fun findOptimumUpshiftPoint(gearsData: List<Gear>): List<Int> {
    val optimumShiftPoints = mutableListOf<Int>()
    for (i in 0 until gearsData.size - 1) {
        val currentGearAcceleration = gearsData[i].accelCurve
        val nextGearAcceleration = gearsData[i + 1].accelCurve

        val maxSize = max(currentGearAcceleration.size, nextGearAcceleration.size)

        for (j in maxSize - 1 downTo 0) {
            val currentExists = j < currentGearAcceleration.size
            val nextExists = j < nextGearAcceleration.size


            if (!nextExists) {
                optimumShiftPoints.add(j)
                break
            } else if (currentExists && currentGearAcceleration[j] >= findAccelFromSpeed(
                    gearsData[i].speedCurve[j],
                    gearsData[i + 1].speedCurve,
                    nextGearAcceleration
                )
            ) {
                optimumShiftPoints.add(j)
                break
            }
        }
    }

    return optimumShiftPoints
}


@SuppressLint("SuspiciousIndentation")
fun estimate_acceleration() {
    val data = getTabsData()

    val measurementUnit = data[0][R.id.car_name_input]?.let { getSpeedMeasureUnit(it.context) }

    // tab 1
    val carName = (data[0][R.id.car_name_input] as? TextInputEditText)?.text?.toString()
    var carMass = (data[0][R.id.car_mass_input] as? TextInputEditText)?.text?.toString()?.toIntOrNull() ?: 0
    val carMassDistribution =
        ((data[0][R.id.car_mass_distribution_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f) / 100f


    // tab 2
    val offClutchRpm = (data[1][R.id.off_clutch_rpm_input] as? TextInputEditText)?.text?.toString()?.toIntOrNull() ?: 0
    var gasStartLevel = ((data[1][R.id.gas_start_level_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f) / 100f
    val drivetrainLoss = ((data[1][R.id.drivetrain_loss_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f) / 100f
    val shiftingTime = (data[1][R.id.shifting_time_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f
    val drivetrainLayout = (data[1][R.id.drivetrain_layout_spinner] as? Spinner)?.selectedItem.toString()
    val (rpmElements, torqueElements) = getRPMTorqueElements()

    val rpmOriginalValues = ArrayList<Double>()
    val torqueOriginalValues = ArrayList<Double>()
    val hpOriginalValues = ArrayList<Double>()

    for (i in rpmElements.indices) {
        val rpmValue = rpmElements[i].text.toString().toDoubleOrNull() ?: 0.0


        var torqueValue = (torqueElements[i].text.toString().toDoubleOrNull()?.times((1 - drivetrainLoss))) ?: 0.0

        // transforming from imperial to metric
        if (measurementUnit != true){
            torqueValue = torqueValue * 1.356
        }


        rpmValue?.let { rpmOriginalValues.add(it) }
        torqueValue?.let { torqueOriginalValues.add(it) }


        if (rpmValue != null && torqueValue != null) {
            val hpValue = (torqueValue * rpmValue) / 7127
            hpOriginalValues.add(hpValue)
        }
    }


    val gearRatiosValues = ArrayList<Float>()
    val gearRatioElements = getGearRatioElements()

    for (i in gearRatioElements.indices) {
        val gearRatioValue = gearRatioElements[i].text.toString().toFloatOrNull()
        gearRatioValue?.let { gearRatiosValues.add(it) }
    }

    val finalDriveRatio = (data[1][R.id.final_drive_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f

    val idlerpm = rpmOriginalValues.firstOrNull() ?: 0.0
    val redlineRpm = rpmOriginalValues.lastOrNull() ?: 0.0
    val total_rpms = redlineRpm - idlerpm


    // tab 3
    val tiresWidth = (data[2][R.id.tires_width_input] as? TextInputEditText)?.text?.toString()?.toIntOrNull() ?: 0
    val tiresAspectRatio = ((data[2][R.id.tires_aspect_ratio_input] as? TextInputEditText)?.text?.toString()?.toIntOrNull() ?: 0) / 100f
    val tiresWheelDiameter = (data[2][R.id.tires_wheel_diameter_input] as? TextInputEditText)?.text?.toString()?.toIntOrNull() ?: 0
    val tiresFrictionCoeff = (data[2][R.id.tires_friction_coeff_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f
    val tiresRollingCoeff = (data[2][R.id.tires_rolling_coeff_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f

    // tab 4
    val aeroDragCoeff =
        (data[3][R.id.aerodynamics_coefficient_of_drag_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f
    var aeroFrontalArea = (data[3][R.id.aerodynamics_frontal_area_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f
    var aeroAirDensity = (data[3][R.id.aerodynamics_air_density_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f


    val aeroNegativeLiftCoeff: Double = when (data.getOrNull(3)?.containsKey(R.id.negative_lift_coefficient_input)) {
        true -> (data[3][R.id.negative_lift_coefficient_input] as? TextInputEditText)?.text?.toString()?.toDoubleOrNull() ?: 0.0
        else -> 0.0 // Handle the case when the specified key is not found in the map
    }
    val aeroDownforceTotalArea: Double = when (data?.getOrNull(3)?.containsKey(R.id.downforce_total_area_input)) {
        true -> (data[3][R.id.downforce_total_area_input] as? TextInputEditText)?.text?.toString()?.toDoubleOrNull() ?: 0.0
        else -> 0.0 // Default value when the key is not found
    }

    val aeroDownforceDistribution: Double = when (data?.getOrNull(3)?.containsKey(R.id.downforce_distribution_input)) {
        true -> ((data[3][R.id.downforce_distribution_input] as? TextInputEditText)?.text?.toString()?.toDoubleOrNull() ?: 0.0)/100
        else -> 0.0 // Default value when the key is not found
    }


    // tab 5
    var initialSpeed = ((data[4][R.id.initial_speed_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f)
    var finalSpeed = ((data[4][R.id.final_speed_input] as? TextInputEditText)?.text?.toString()?.toFloatOrNull() ?: 0f)


    val rpmInterpolated = linearInterpolation(rpmOriginalValues, total_rpms)
    val torqueInterpolated = linearInterpolation(torqueOriginalValues, total_rpms)
    val hpInterpolated = linearInterpolation(hpOriginalValues, total_rpms)


    // transforming from imperial to metric
    if (measurementUnit != true){
        initialSpeed = (initialSpeed / 2.237).toFloat()
        finalSpeed = (finalSpeed / 2.237).toFloat()
        aeroAirDensity = (aeroAirDensity / 16.02).toFloat()
        carMass = (carMass / 2.205).toInt()
        aeroFrontalArea = (aeroFrontalArea /0.0929).toFloat()

    }else{
        initialSpeed = (initialSpeed / 3.6).toFloat()
        finalSpeed = (finalSpeed / 3.6).toFloat()
    }


    val g = 9.81 // Gravitational Acceleration m/s


    // limit of adhesion of tires in terms of max allowed force before entering dynamic friction
    // not including downforce yet since its not calculated
    val maxTractiveForce: Double = when (drivetrainLayout) {
        "RWD" -> g * carMass * (1.0 - carMassDistribution) * tiresFrictionCoeff
        "FWD" -> g * carMass * carMassDistribution * tiresFrictionCoeff
        "AWD" -> g * carMass * tiresFrictionCoeff
        else -> 0.0 // Handle the case when drivetrainLayout is not any of the specified values
    }

    var gears = ArrayList<Gear>()

    //tire diameter in inch
    val tireDiameter = (tiresWidth * tiresAspectRatio * 2) / 25.4 + tiresWheelDiameter

    //tire radius in meters (diameter in inch divided by 2 to get the radius)
    val tireRadius = tireDiameter / 39.37 / 2


    for (i in 0 until gearRatiosValues.size) {
        var speedCurve = ArrayList<Double>()
        var airResistanceCurve = ArrayList<Double>()
        var downforceCurve = ArrayList<Double>()
        var wheelTorqueCurve = ArrayList<Double>()
        var accelCurve = ArrayList<Double>()

        for (j in 0 until rpmInterpolated.size) {
            val speedValue = (rpmInterpolated[j] * tireDiameter) / (gearRatiosValues[i] * finalDriveRatio * 336) * 1.609 / 3.6


            val airResValue = (aeroDragCoeff * aeroFrontalArea * aeroAirDensity * speedValue * speedValue) / 2
            if (data.getOrNull(3)?.containsKey(R.id.negative_lift_coefficient_input) == true) {
                val downforceValue =
                    (((aeroNegativeLiftCoeff.times(aeroDownforceTotalArea)).times(aeroAirDensity) ?: 0.0) * speedValue * speedValue) / 2
                downforceCurve.add(downforceValue)
            }

            val wheelTorqueValue = (torqueInterpolated[j] * gearRatiosValues[i] * finalDriveRatio)
            speedCurve.add(speedValue)
            airResistanceCurve.add(airResValue)
            wheelTorqueCurve.add(wheelTorqueValue)

        }

        for (j in 0 until rpmInterpolated.size) {
            val rollingResistance = tiresRollingCoeff * g * carMass
            val accelerationForce = ((wheelTorqueCurve[j] / tireRadius) - airResistanceCurve[j] - rollingResistance)


            var downforceOnDrivenAxle: Double = 0.0

            if (data.getOrNull(3)?.containsKey(R.id.negative_lift_coefficient_input) == true) {
                downforceOnDrivenAxle = when (drivetrainLayout) {
                    "RWD" -> downforceCurve[j] * (1 - aeroDownforceDistribution)
                    "FWD" -> downforceCurve[j] * aeroDownforceDistribution
                    "AWD" -> downforceCurve[j]
                    else -> 0.0 // Handle the case when drivetrainLayout is not any of the specified values
                }
            }


            // max allowed force the car can put down before slipping into dynamic friction
            val maxTraction = maxTractiveForce + (downforceOnDrivenAxle * tiresFrictionCoeff)
            // in this estimation we do the maximum possible acceleration that the car allows under static friction
            // thus if the car has more torque pushing than the tires can take before slipping, we are going to use
            // the maximum force the tires can take, assuming as if there is a traction system limiting
            // the torque sent to the wheels so that it does not slip.
            // In reality,a car accelerate best when there is some slip, but that simulation is a lot more complicated
            // and there also isnt enough tire data to even attempt to do that.
            val accelerationValue = minOf(maxTraction, accelerationForce) / carMass

            if (accelerationValue >= 0) {
                accelCurve.add(accelerationValue)
            } else {
                break
            }
        }

        gears.add(
            Gear(
                gearRatiosValues[i],
                accelCurve,
                rpmInterpolated,
                speedCurve,
                wheelTorqueCurve,
                airResistanceCurve,
                downforceCurve,
                hpInterpolated,
                finalDriveRatio,
                tireRadius,
                carMass
            )
        )

    }


    val optimumUpshiftValues = findOptimumUpshiftPoint(gears)
    for (i in 0 until optimumUpshiftValues.size) {
        gears[i].addOptimumUpshift(optimumUpshiftValues[i])
        gears[i].topSpeed?.let { searchsorted(gears[i + 1].speedCurve, it.toDouble()) }?.let { gears[i].addDropdownRpm(it) }
    }

    var currentSpeed = initialSpeed
    var totalTime = 0.0

    //finding start parameters
    var currentGear = -1  // Initialize outside the loop
    var currentRpm = -1   // Initialize outside the loop

    for (i in 0 until gearRatiosValues.size) {
        val idx = searchsorted(gears[i].speedCurve, currentSpeed.toDouble())
        if (idx < gears[i].speedCurve.size) {
            currentGear = i  // Assign value inside the loop
            currentRpm = idx // Assign value inside the loop
            break
        }
    }
    if (currentGear == -1 && currentRpm == -1) {
        AlertDialog.Builder(data[1][R.id.off_clutch_rpm_input]?.context)
            .setMessage("Initial speed is too high. The top speed of the car is lower than the initial speed inputted.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
        return
    }

    var possibleFinalSpeed = false
    for (i in 0 until gearRatiosValues.size) {
        val idx = searchsorted(gears[i].speedCurve, finalSpeed.toDouble())
        if (idx < gears[i].speedCurve.size) {
            possibleFinalSpeed = true
            break
        }
    }
    if (!possibleFinalSpeed) {

        AlertDialog.Builder(data[4][R.id.initial_speed_input]?.context)
            .setMessage("Final speed is too high. The top speed of the car is lower than the final speed inputted.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
        return
    }


    var numberOfSteps = 0 // Initialize with appropriate default values
    var frictionForce = 0.0
    var pushingForce = 0.0
    var clutchDepression = 0.0
    var gasStep = 0f
    var clutchStep = 0.0


    // in this case we need to engage the clutch
    if (currentRpm <= offClutchRpm) {
        var numberOfSteps = offClutchRpm - currentRpm
        var frictionForce = tiresRollingCoeff * g * carMass // minimum necessary force to start moving the car
        var pushingForce = gears[currentGear].wheelTorque[currentRpm] / tireRadius
        var clutchDepression = 1 - (frictionForce / (pushingForce * gasStartLevel))
        if (clutchDepression >= 1) {
            AlertDialog.Builder(data[4][R.id.initial_speed_input]?.context)
                .setMessage("There isn't enough force to start moving the car. Try increasing Gas Start Level or change Off Clutch RPM.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()

            return
        }
        val gasStep = (1 - gasStartLevel) / numberOfSteps
        val clutchStep = (1 - clutchDepression) / numberOfSteps
    }


    while (currentSpeed < finalSpeed) {

        //shifting up
        if (currentRpm == gears[currentGear].upshiftRpm) {
            totalTime += shiftingTime
            currentRpm = gears[currentGear].dropdownRpm!!
            currentGear += 1

        }

        // standing from standstill
        if (currentRpm <= offClutchRpm) {
            while (currentRpm <= offClutchRpm) {

                if (currentRpm != 0) {   // 0 meaning idle_rpm
                    totalTime += ((gears[currentGear].speedCurve[currentRpm] - gears[currentGear].speedCurve[(currentRpm - 1)]) / (gears[currentGear].accelCurve[currentRpm] * gasStartLevel * (1 - clutchDepression)))
                } else {
                    totalTime += (gears[currentGear].speedCurve[currentRpm] / (gears[currentGear].accelCurve[currentRpm] * gasStartLevel * (1 - clutchDepression)))
                }
                currentSpeed = gears[currentGear].speedCurve[currentRpm].toFloat()

                currentRpm += 1
                gasStartLevel += gasStep
                clutchDepression -= clutchStep
            }
        }

        //accelerating
        if (currentRpm != 0) {   // 0 meaning idle_rpm
            totalTime += ((gears[currentGear].speedCurve[currentRpm] - gears[currentGear].speedCurve[(currentRpm - 1)]) / gears[currentGear].accelCurve[currentRpm])
        } else {
            totalTime += (gears[currentGear].speedCurve[currentRpm] / gears[currentGear].accelCurve[currentRpm])
        }
        currentSpeed = gears[currentGear].speedCurve[currentRpm].toFloat()
        currentRpm += 1
    }

    setGearObjects(gears)

    var topSpeed = 0
    for (i in 0 until gearRatiosValues.size) {
        if (topSpeed < gears[i].speedCurve[gears[i].accelCurve.size - 1]) {
            topSpeed = gears[i].speedCurve[gears[i].accelCurve.size - 1].toInt()
        }

    }




    val speedMeasureUnit: String?
    var formattedInitialSpeed: Int
    var formattedFinalSpeed: Int
    var formattedTopSpeed: Int

    if (measurementUnit == true) {
        speedMeasureUnit = data[4][R.id.final_speed_input]?.context?.getString(R.string.speed_measure_unit)

        // for metric only, must implement case for imperial
        formattedInitialSpeed = (initialSpeed * 3.6).toInt()
        formattedFinalSpeed = (finalSpeed * 3.6).toInt()
        formattedTopSpeed = (topSpeed * 3.6).toInt()
    } else {
        speedMeasureUnit = data[4][R.id.final_speed_input]?.context?.getString(R.string.speed_measure_unit_imperial)

        // for metric only, must implement case for imperial
        formattedInitialSpeed = (initialSpeed * 2.23).toInt()
        formattedFinalSpeed = (finalSpeed * 2.23).toInt()
        formattedTopSpeed = (topSpeed * 2.23).toInt()
    }
    
    var formattedTime = "%.1f".format(totalTime)


    val resultString = StringBuilder()
    resultString.append("${carName} accelerated from ${formattedInitialSpeed} ${speedMeasureUnit} to ${formattedFinalSpeed} ${speedMeasureUnit} in: ${formattedTime}s.\n\n")

    for (index in 0 until gears.size - 1) {
        resultString.append("Gear ${index + 1} Optimum Upshift: ${gears[index].upshiftRpm?.plus(1)?.plus(idlerpm)?.toInt()} \n")
    }
    resultString.append("\n\n")
    resultString.append("Top speed: ${formattedTopSpeed} ${speedMeasureUnit}")


    AlertDialog.Builder(data[4][R.id.final_speed_input]?.context ?: return)
        .setMessage(resultString)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()

}


private fun getSettingsFile(context: Context): File {
    val folder = File(context.filesDir, settingsFolder)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return File(folder, settingsFileName)
}

fun getSpeedMeasureUnit(context: Context): Boolean {
    val settingsFile = getSettingsFile(context)
    if (settingsFile.exists()) {
        val content = settingsFile.readText().trim().toBoolean()
        return content
    } else {
        settingsFile.writeText("true")
        return true // Default value
    }
}


fun setSpeedMeasureUnit(context: Context, unit: Boolean) {
    val settingsFile = getSettingsFile(context)
    settingsFile.writeText(unit.toString())
}