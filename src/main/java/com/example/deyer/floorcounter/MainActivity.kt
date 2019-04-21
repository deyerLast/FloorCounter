package com.example.deyer.floorcounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager

    //saving
    var data = arrayListOf<String>()
    var xData = arrayListOf<Double>()
    var yData = arrayListOf<Double>()
    var zData = arrayListOf<Double>()

    //var finalData = arrayListOf<Double>(xData[0], yData[1], zData[2])

    var stepCount = 0
    var climbCount = 0

    var pythag:Double = 0.0
    var pythageUnfilterd:Double = 0.0
    var infoPythagUnfiltered = arrayListOf<Double>()

    var pythagfiltered = arrayListOf<Double>()

    var sensorEventCount = 0
    var filterEventCount = 0

    private var stepClicked = false
    private var climbClicked = false

    //These are the values to save, or think about using these to save
    var infoStep = arrayListOf<String>()
    var infoClimb = arrayListOf<String>()

    // file to save step data
    var fileStep = File(
        Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS), "step.txt")
    // file to save climb data
    var fileClimb = File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS), "climb.txt")
    // file to save unfiltered pythag data
    var filePythagUnfiltered = File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS), "pythagoreanUnfiltered.txt")
    // file to save filtered pythag data
    var filePythag = File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS), "pythagoreanFiltered.txt")



    //==============================================================================================================================

    override fun onPause() {
        super.onPause()
        stepClicked = false
        climbClicked = false
        sensorManager?.unregisterListener(this)
    }

    //==============================================================================================================================

    override fun onResume() {
        super.onResume()

    }

    //==============================================================================================================================

    override fun onSensorChanged(event: SensorEvent?) {
       acceler.text = "x = ${event!!.values[0]}\n\n" +
                "y = ${event.values[1]}\n\n" +
                "z = ${event.values[2]}"
        //xData = (if (event != null) event else throw NullPointerException("Expression 'event' must not be null")).values[0]
        data.add("x = ${event!!.values[0]}\n\n" +
                "y = ${event.values[1]}\n\n" +
                "z = ${event.values[2]}")



        //Climb Using Pythag
        pythag = (event.values[0].toDouble()*event.values[0].toDouble())+
                (event.values[1].toDouble()*event.values[1].toDouble())+
                (event.values[2].toDouble()*event.values[2].toDouble())
        pythageUnfilterd = sqrt(pythag) //Combination of all the axis

        //pythag thm. data into an array
        infoPythagUnfiltered.add(pythageUnfilterd)


        //The below code is not working yet
//WHat is wrong with the valleys?

        //peaks and valleys
 /*       if(infoPythagUnfiltered.size > 3) {

            if ((infoPythagUnfiltered[sensorEventCount-1] > infoPythagUnfiltered[sensorEventCount-2]) &&
                (infoPythagUnfiltered[sensorEventCount-1] > infoPythagUnfiltered[sensorEventCount]) ) {

                pythagfiltered.add((infoPythagUnfiltered[sensorEventCount - 1]))
                filterEventCount += 1

            }
            if ((infoPythagUnfiltered[sensorEventCount - 1] < infoPythagUnfiltered[sensorEventCount - 2]) &&
                (infoPythagUnfiltered[sensorEventCount - 1] < infoPythagUnfiltered[sensorEventCount]) ) {

                pythagfiltered.add((infoPythagUnfiltered[sensorEventCount - 1]))
                filterEventCount += 1
            }
        }
*/

        // just testing a method to see if i could only count peaks but peaks don't represent steps
        if (pythagfiltered.size > 3) {

            if ( (pythagfiltered[filterEventCount-1] > pythagfiltered[filterEventCount-2]) &&
                (pythagfiltered[filterEventCount-1] > pythagfiltered[filterEventCount]) &&
                (pythagfiltered[filterEventCount-1] > 10.5) ) {
                if (stepClicked == true) {
                    stepCount +=1

                    Log.i ("info step counter", stepCount.toString())
                    Log.i ("info step pF -1", pythagfiltered[filterEventCount-1].toString())
                    Log.i ("info step pF -2", pythagfiltered[filterEventCount-2].toString())
                    Log.i ("info step pF", pythagfiltered[filterEventCount-0].toString())

                }

                if (climbClicked == true) {
                    climbCount += 1
                    Log.i ("info climb counter", climbCount.toString())

                }
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




       // WriteToFile("$data\n What's Going ON!!? \n")

    }

    //==============================================================================================================================

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
                climbClicked = true
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)= when(item.itemId) {


        R.id.view -> {
            //view button is to just view the list of sensor data but it is not implemented yet

            var textD: TextView = findViewById(R.id.text1)
            textD.visibility = View.INVISIBLE

            var textC:TextView = findViewById(R.id.climbCount)
            textC.visibility = View.INVISIBLE

            var textS:TextView = findViewById(R.id.stepCount)
            textS.visibility = View.INVISIBLE

            var bC:Button = findViewById(R.id.climb)
            bC.visibility = View.INVISIBLE
            var bS:Button = findViewById(R.id.step)
            bS.visibility = View.INVISIBLE

            var data: ListView = findViewById(R.id.list)
            data.visibility = View.VISIBLE

            true
        }

        R.id.write -> {
            //used to save all the arrays to files if the save button is pressed

            fileStep.writeText("walk =  " + infoStep.toString())
            infoStep.clear()

            fileClimb.writeText("climb =  " + infoClimb.toString())
            infoClimb.clear()

            filePythag.writeText("Pythagorean Filtered Data (X,Y,Z) =  " + pythagfiltered.toString())
            pythagfiltered.clear()

            //filePythagUnfiltered.writeText("Pythagorean Unfiltered Data (X, Y, Z) =  " + infoPythagUnfiltered.toString())
            infoPythagUnfiltered.clear()

            true
        }

        R.id.clear -> {
            //used to save all the arrays to files if the save button is pressed

            infoStep.clear()
            infoClimb.clear()
            //infoPythagFiltered.clear()
            infoPythagUnfiltered.clear()

            true
        }

        R.id.back -> {
            // sets list to invisible and goes back to original state
            var textD:TextView = findViewById(R.id.text1)
            textD.visibility = View.VISIBLE
            var textC:TextView = findViewById(R.id.climbCount)
            textC.visibility = View.VISIBLE

            var textS:TextView = findViewById(R.id.stepCount)
            textS.visibility = View.VISIBLE

            var bC:Button = findViewById(R.id.climb)
            bC.visibility = View.VISIBLE
            var bS:Button = findViewById(R.id.step)
            bS.visibility = View.VISIBLE

            var data:ListView = findViewById(R.id.list)
            data.visibility = View.INVISIBLE

            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }

    }


    //==================================================================================================================

    fun WriteToFile(str: ArrayList<String>){
        try{
           // var fo = FileWriter("floorCounterSaveFile.txt")
            val fileName = "src/resources/savedData.txt"
            val myFile = File(fileName)

            myFile.printWriter().use{
                out ->
                out.println(str + "\n")

                println("Wrote to file: src/resources/savedData.tx")
            }

            //println("Wrote to file: src/resources/savedData.tx")

        } catch (ex:Exception){
            print(ex.message)
        }
    }
}
