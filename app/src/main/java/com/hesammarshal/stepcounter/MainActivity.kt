package com.hesammarshal.stepcounter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hesammarshal.stepcounter.databinding.ActivityMainBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class MainActivity : AppCompatActivity(), SensorEventListener {


    private lateinit var binding: ActivityMainBinding
    private var sensorManager : SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_main)

        //Binding the xml items
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Info", "No Permisson - request for Permission")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                1408)

            Toast.makeText(this, "Request for Per", Toast.LENGTH_SHORT).show()

        }


        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager



    }
    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepSensor == null){
            // The Phone does not support step counter
            // Log.d("Info", "stepSensor is null")
            Toast.makeText(this, "No Sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // It use by delay lead to less use of resources
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (running){
            totalSteps = p0!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            binding.tvStepsTaken.text = ("$currentSteps")


            val circularProgressBar = findViewById<CircularProgressBar>(R.id.progress_circular)
            circularProgressBar.apply{
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    private fun resetSteps() {
        binding.tvStepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.tvStepsTaken.setOnLongClickListener{
            previousTotalSteps = totalSteps
            binding.tvStepsTaken.text = 0.toString()
            saveData()
            true
        }
    }

    private fun saveData(){
        val sharedPreferences = getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        //Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}