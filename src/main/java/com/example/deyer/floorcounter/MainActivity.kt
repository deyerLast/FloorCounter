package com.example.deyer.floorcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager

    //saving
    var data = ""
    var xData = arrayListOf<Double>()
    var yData = arrayListOf<Double>()
    var zData = arrayListOf<Double>()

    //var finalData = arrayListOf<Double>(xData[0], yData[1], zData[2])

    var stepCount = 0
    var climbCount = 0

    var pythag:Double = 0.0
    var pythageUnfilterd:Double = 0.0
    var infoPythagUnfiltered = arrayListOf<Double>()

    var sensorEventCount = 0
    var filterEventCount = 0

    private var stepClicked = false
    private var climbClicked = false

    //These are the values to save, or think about using these to save
    var infoStep = arrayListOf<String>()
    var infoClimb = arrayListOf<String>()


    override fun onPause() {
        super.onPause()
        stepClicked = false
        climbClicked = false
        sensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

    }




    override fun onSensorChanged(event: SensorEvent?) {
       acceler.text = "x = ${event!!.values[0]}\n\n" +
                "y = ${event.values[1]}\n\n" +
                "z = ${event.values[2]}"
        //xData = (if (event != null) event else throw NullPointerException("Expression 'event' must not be null")).values[0]
        data = acceler.toString()



        //Climb Using Pythag
        pythag = (event.values[0].toDouble()*event.values[0].toDouble())+
                (event.values[1].toDouble()*event.values[1].toDouble())+
                (event.values[2].toDouble()*event.values[2].toDouble())
        pythageUnfilterd = sqrt(pythag) //Combination of all the axis

        //filter pythag thm. data into an array
        infoPythagUnfiltered.add(pythageUnfilterd)


        //The below code is not working yet
//WHat is wrong with the valleys?

        //peaks and valleys
        if(infoPythagUnfiltered.size > 3) {

            if ((infoPythagUnfiltered[sensorEventCount-1] > infoPythagUnfiltered[sensorEventCount-2]) &&
                (infoPythagUnfiltered[sensorEventCount-1] > infoPythagUnfiltered[sensorEventCount]) ) {

                infoPythagUnfiltered.add((infoPythagUnfiltered[sensorEventCount - 1]))
                filterEventCount += 1

            }
            if ((infoPythagUnfiltered[sensorEventCount - 1] < infoPythagUnfiltered[sensorEventCount - 2]) &&
                (infoPythagUnfiltered[sensorEventCount - 1] < infoPythagUnfiltered[sensorEventCount]) ) {

                infoPythagUnfiltered.add((infoPythagUnfiltered[sensorEventCount - 1]))
                filterEventCount += 1
            }
        }






        // array to store steps adding event values to is if step button is pressed
        if (stepClicked == true) {
            infoStep.add(" x = ${event!!.values[0]}, " +
                    " y = ${event.values[1]}, " +
                    " z = ${event.values[2]}  +")
        }
        // array to store stair steps adding event values to is if climb button is pressed
        if (climbClicked == true) {
            infoClimb.add(" x = ${event!!.values[0]}, " +
                    " y = ${event.values[1]}, " +
                    " z = ${event.values[2]}  +")
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    //==============================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //This is what i need to do to make a menu
        //setSupportActionBar(toolbar)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        var climbButton: Button = findViewById(R.id.climbButton)
        var stepButton: Button = findViewById(R.id.walkButton)

        stepButton.setOnClickListener {
            if (stepClicked == false){
                stepClicked == true
                if(climbClicked == true){
                    climbClicked = false
                }
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL)
            } else if (stepClicked == true){
                stepClicked = false
                sensorManager.unregisterListener(this)
            }
        }

        climbButton.setOnClickListener {
            if(climbClicked == false){
                climbClicked == true
                if(stepClicked == true){
                    stepClicked = false
                }
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST)
            } else if(climbClicked == true){
                climbClicked = false
                sensorManager.unregisterListener(this)
            }
        }
        //var save = findViewById<Button>(R.id.save)

    } // End onCreate

    //==================================================================================================================

    fun WriteToFile(str:String){
        try{
           // var fo = FileWriter("floorCounterSaveFile.txt")
            val fileName = FileWriter("src/resources/savedData.txt")
            fileName.write(str + "\n")
            fileName.close()
        } catch (ex:Exception){
            print(ex.message)
        }
    }
}
