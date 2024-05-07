
package com.example.accelerationestimator.ui

import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.accelerationestimator.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class Graphs : AppCompatActivity() {

    lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)


        lineChart=findViewById(R.id.line_chart)
        graphType = getGraphTypes()
        gearData = getGearObjects()

        var xAxis = findViewById<TextView>(R.id.x_axis_label)
        var yAxis = findViewById<TextView>(R.id.y_axis_label)

        val distinctColors = listOf(
            Color.parseColor("#3d34eb"), // Blue
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#b3ff00"), // Lime
            Color.parseColor("#00BCD4")  // Cyan
        )

        val colorsIntArray = distinctColors.toIntArray()

        when(graphType){
            0 -> { // Engine graph

                xAxis.text = "RPM"
                yAxis.text = "Torque (N) / Power (HP)"


                val torqueVsRpm: ArrayList<Entry> = ArrayList()
                val hpVsRpm: ArrayList<Entry> = ArrayList()

                val lineData = LineData()
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }

                val torqueMeasurementUnit: Double
                if (!measurementUnit) {
                    torqueMeasurementUnit = 2.23
                    yAxis.text = "Torque (lb ft) / Power (HP)"


                } else {
                    torqueMeasurementUnit = 1.0
                    yAxis.text = "Torque (N) / Power (HP)"

                }
                for (j in 0 until (gearData?.get(0)?.accelCurve?.size ?: 0)) {

                    torqueVsRpm.add(
                        Entry(
                            gearData!![0].rpmCurve[j].toFloat(),
                            (((gearData!![0].wheelTorque[j]) / gearData!![0].gearRatio / gearData!![0].finalDrive).toFloat()/torqueMeasurementUnit).toFloat()
                        )
                    )
                    hpVsRpm.add(Entry(gearData!![0].rpmCurve[j].toFloat(), (gearData!![0].hpCurve[j].toFloat())))

                }

                val torqueDataSet = LineDataSet(torqueVsRpm, "Torque (Nm)")
                val hpDataSet = LineDataSet(hpVsRpm, "Horsepower (HP)")

                torqueDataSet.setDrawCircles(false)
                torqueDataSet.setDrawValues(false)
                torqueDataSet.setColor(colorsIntArray[0], 255);

                hpDataSet.setDrawCircles(false)
                hpDataSet.setDrawValues(false)
                hpDataSet.setColor(colorsIntArray[1], 255);

                lineData.addDataSet(torqueDataSet)
                lineData.addDataSet(hpDataSet)

                lineChart.data = lineData
                lineChart.description.text = "Engine Torque & HP Graph"
                lineChart.animateY(1000)

            }


            1 ->{ //Acceleration vs Speed
                yAxis.text = "Acceleration (g)"
                val lineData = LineData()
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    xAxis.text = "Speed (mph)"

                } else {
                    speedMeasureUnitMultiplier = 3.6
                    xAxis.text = "Speed (kmh)"
                }
                for (i in 0 until (gearData?.size ?: 0)){
                    val accelVsRpm: ArrayList<Entry> = ArrayList()
                    for (j in 0 until (gearData?.get(i)?.accelCurve?.size ?: 0)) {
                        accelVsRpm.add(Entry(
                            (gearData!![i].speedCurve[j].toFloat() * speedMeasureUnitMultiplier).toFloat(),
                            (((gearData!![i].accelCurve[j].toFloat())) / 9.81).toFloat()
                        ))
                    }

                    val accelVsRpmSet = LineDataSet(accelVsRpm, "Gear ${i+1}")
                    accelVsRpmSet.setDrawCircles(false)
                    accelVsRpmSet.setDrawValues(false)
                    accelVsRpmSet.setColor(colorsIntArray[i], 255);
                    lineData.addDataSet(accelVsRpmSet)
                }

                lineChart.data = lineData
                lineChart.description.text = "Acceleration VS RPM"
                lineChart.animateY(1000)
            }

            2 ->{ // Wheel torque
                val lineData = LineData()
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                val torqueMeasureUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    xAxis.text = "Speed (mph)"
                    yAxis.text = "Torque (lbf)"
                    torqueMeasureUnitMultiplier = 1.356
                } else {
                    speedMeasureUnitMultiplier = 3.6
                    xAxis.text = "Speed (kmh)"
                    yAxis.text = "Torque (N)"
                    torqueMeasureUnitMultiplier = 1.0
                }

                for (i in 0 until (gearData?.size ?: 0)){
                    val torqueVsRpm: ArrayList<Entry> = ArrayList()
                    for (j in 0 until (gearData?.get(i)?.accelCurve?.size ?: 0)) {

                        torqueVsRpm.add(Entry(
                            (gearData!![i].speedCurve[j].toFloat() * speedMeasureUnitMultiplier).toFloat(),
                            ((gearData!![i].accelCurve[j] * gearData!![i].carMass).toFloat()/ torqueMeasureUnitMultiplier).toFloat()
                        ))
                    }

                    val accelVsRpmSet = LineDataSet(torqueVsRpm, "Gear ${i+1}")
                    accelVsRpmSet.setDrawCircles(false)
                    accelVsRpmSet.setDrawValues(false)
                    accelVsRpmSet.setColor(colorsIntArray[i], 255);
                    lineData.addDataSet(accelVsRpmSet)
                }

                lineChart.data = lineData
                lineChart.description.text = "Torque VS Speed"
                lineChart.animateY(1000)
            }



            3 ->{ // Wheel torque (No opposing forces)

                val lineData = LineData()
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                val torqueMeasureUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    xAxis.text = "Speed (mph)"
                    yAxis.text = "Torque (lbf)"
                    torqueMeasureUnitMultiplier = 1.356
                } else {
                    speedMeasureUnitMultiplier = 3.6
                    xAxis.text = "Speed (kmh)"
                    yAxis.text = "Torque (N)"
                    torqueMeasureUnitMultiplier = 1.0
                }


                for (i in 0 until (gearData?.size ?: 0)){
                    val torqueVsRpm: ArrayList<Entry> = ArrayList()
                    for (j in 0 until (gearData?.get(i)?.accelCurve?.size ?: 0)) {

                        torqueVsRpm.add(Entry(
                            (gearData!![i].speedCurve[j].toFloat()*speedMeasureUnitMultiplier).toFloat(),
                            (((((gearData!![i].wheelTorque[j].toFloat()))/ gearData!![i].tireRadius).toFloat())/torqueMeasureUnitMultiplier).toFloat()
                        ))
                    }

                    val accelVsRpmSet = LineDataSet(torqueVsRpm, "Gear ${i+1}")
                    accelVsRpmSet.setColor(colorsIntArray[i], 255);
                    accelVsRpmSet.setDrawCircles(false)
                    accelVsRpmSet.setDrawValues(false)
                    lineData.addDataSet(accelVsRpmSet)
                }


                lineChart.data = lineData
                lineChart.description.text = "Torque VS Speed"
                lineChart.animateY(1000)
            }


            4 ->{ // Just Gearing

                val lineData = LineData()
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    xAxis.text = "Speed (mph)"
                } else {
                    speedMeasureUnitMultiplier = 3.6
                    xAxis.text = "Speed (kmh)"
                }

                for (i in 0 until (gearData?.size ?: 0)){
                    val gearSpeed: ArrayList<Entry> = ArrayList()
                    for (j in 0 until (gearData?.get(i)?.speedCurve?.size ?: 0)) {
                        gearSpeed.add(Entry((gearData!![i].speedCurve[j].toFloat() * speedMeasureUnitMultiplier).toFloat(),(gearData!![i].rpmCurve[j].toFloat())))
                    }

                    val accelVsRpmSet = LineDataSet(gearSpeed, "Gear ${i+1}")
                    accelVsRpmSet.setDrawCircles(false)
                    accelVsRpmSet.setDrawValues(false)
                    accelVsRpmSet.setColor(colorsIntArray[i], 255);
                    lineData.addDataSet(accelVsRpmSet)

                }

                lineChart.data = lineData
                lineChart.description.text = "Gear Speeds (No opposing forces)"
                lineChart.animateY(1000)
            }



            5 ->{ // Air Resistance
                val lineData = LineData()
                val airResVsSpeed: ArrayList<Entry> = ArrayList()

                var topSpeed = 0
                var indexGear = 0
                for (i in 0 until (gearData?.size ?: 0)) {
                    if(topSpeed < gearData?.get(i)!!.speedCurve[gearData!![i].accelCurve.size - 1]){
                        topSpeed = gearData?.get(i)!!.speedCurve[gearData!![i].accelCurve.size - 1].toInt()
                        indexGear = i
                    }

                }
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                val forceUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    forceUnitMultiplier = 4.448


                } else {
                    speedMeasureUnitMultiplier = 3.6
                    forceUnitMultiplier = 1.0

                }

                for (j in 0 until (gearData?.get(0)?.accelCurve?.size ?: 0)) {

                    airResVsSpeed.add(Entry((gearData!![indexGear].speedCurve[j].toFloat() * speedMeasureUnitMultiplier).toFloat(),
                        (gearData!![indexGear].airResistanceCurve[j].toFloat()/ forceUnitMultiplier).toFloat()
                    ))
                }
                val airResSet: LineDataSet
                if (!measurementUnit) {
                    airResSet = LineDataSet(airResVsSpeed, "Air Resistance (lbf)")
                    xAxis.text = "Speed (mph)"
                    yAxis.text = "Air Resistance (lbf)"
                } else {
                    xAxis.text = "Speed (kmh)"
                    yAxis.text = "Air Resistance (N)"
                    airResSet = LineDataSet(airResVsSpeed, "Air Resistance (N)")
                }

                airResSet.setColor(colorsIntArray[3], 255);
                airResSet.setDrawCircles(false)
                airResSet.setDrawValues(false)
                lineData.addDataSet(airResSet)

                lineChart.data = lineData
                lineChart.description.text = "Air Resistance"
                lineChart.animateY(1000)
            }

            6 ->{ // Downforce
                val lineData = LineData()
                val downforceVsSpeed: ArrayList<Entry> = ArrayList()

                var topSpeed = 0
                var indexGear = 0
                for (i in 0 until (gearData?.size ?: 0)) {
                    if(topSpeed < gearData?.get(i)!!.speedCurve[gearData!![i].accelCurve.size - 1]){
                        topSpeed = gearData?.get(i)!!.speedCurve[gearData!![i].accelCurve.size - 1].toInt()
                        indexGear = i
                    }

                }
                val measurementUnit = lineChart.let { getSpeedMeasureUnit(it.context) }
                val speedMeasureUnitMultiplier: Double
                val forceUnitMultiplier: Double
                if (!measurementUnit) {
                    speedMeasureUnitMultiplier = 2.23
                    forceUnitMultiplier = 4.448
                } else {
                    speedMeasureUnitMultiplier = 3.6
                    forceUnitMultiplier = 1.0
                }


                if((gearData?.get(0)?.downforceCurve?.size ?: 0) != 0) {
                    for (j in 0 until (gearData?.get(indexGear)?.accelCurve?.size ?: 0)) {
                        downforceVsSpeed.add(Entry((gearData!![indexGear].speedCurve[j].toFloat()* speedMeasureUnitMultiplier).toFloat(),
                            (gearData!![indexGear].downforceCurve[j].toFloat()/forceUnitMultiplier).toFloat()
                        ))
                    }


                    var downforceVsSpeedSet: LineDataSet

                    if (!measurementUnit) {
                        downforceVsSpeedSet = LineDataSet(downforceVsSpeed, "Downforce (N)")
                        xAxis.text = "Speed (mph)"
                        yAxis.text = "Downforce (lbf)"
                    } else {
                        xAxis.text = "Speed (kmh)"
                        yAxis.text = "Downforce (N)"
                        downforceVsSpeedSet = LineDataSet(downforceVsSpeed, "Downforce (N)")
                    }

                    downforceVsSpeedSet.setColor(colorsIntArray[2], 255);
                    downforceVsSpeedSet.setDrawCircles(false)
                    downforceVsSpeedSet.setDrawValues(false)
                    lineData.addDataSet(downforceVsSpeedSet)


                    lineChart.data = lineData
                    lineChart.description.text = "Downforce"
                    lineChart.animateY(1000)
                }else{

                    AlertDialog.Builder(this)
                        .setMessage("This car does not have Downforce configured.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            onBackPressed()
                        }
                        .show()
                }
            }
            else ->{

            }


        }



    }


}
