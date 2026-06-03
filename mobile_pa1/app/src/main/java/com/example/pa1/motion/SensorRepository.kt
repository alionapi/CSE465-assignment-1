package com.example.pa1.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

data class LiveSensorData(
    val ax: Float = 0f, val ay: Float = 0f, val az: Float = 0f,
    val gx: Float = 0f, val gy: Float = 0f, val gz: Float = 0f
)
data class WindowFeatures(
    val accelMean: Float,
    val accelStd: Float,
    val accelMax: Float,
    val gyroMean: Float,
    val gyroStd: Float,
    val gyroMax: Float,
    val accelZ_mean: Float,
    val stepRate: Float
)
data class SensorReading(
    val ax: Float, val ay: Float, val az: Float,
    val gx: Float, val gy: Float, val gz: Float,
    val timestamp: Long = System.currentTimeMillis()
)
class SensorRepository(private val context: Context) : SensorEventListener {

    companion object {
        const val WINDOW_SIZE = 50     // ~1 second at 50 Hz
        const val SAMPLE_RATE_US = 20_000  // 50 Hz
    }
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val window = ArrayDeque<SensorReading>(WINDOW_SIZE + 1)
    private var latestAccel = FloatArray(3) { 0f }
    private var latestGyro  = FloatArray(3) { 0f }
    private var isListening = false
    private val _liveData = MutableStateFlow(LiveSensorData())
    val liveData = _liveData.asStateFlow()
    private val _features = MutableStateFlow<WindowFeatures?>(null)
    val features = _features.asStateFlow()
    private val _sampleCount = MutableStateFlow(0)
    val sampleCountFlow = _sampleCount.asStateFlow()
    private var csvWriter: FileWriter? = null
    private var currentLabel: String = "unknown"
    private var sampleCount = 0

    fun startListening() {
        if (isListening) return
        sensorManager.registerListener(this, accelSensor, SAMPLE_RATE_US)
        sensorManager.registerListener(this, gyroSensor,  SAMPLE_RATE_US)
        isListening = true
    }
    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        isListening = false
    }
    fun startRecording(label: String) {
        currentLabel = label
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val ts  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "HAR_${label}_$ts.csv")
        csvWriter = FileWriter(file, false)
        csvWriter!!.write("timestamp,ax,ay,az,gx,gy,gz,label\n")
        sampleCount = 0
        _sampleCount.value = 0
    }
    fun stopRecording(): String {
        csvWriter?.flush()
        csvWriter?.close()
        csvWriter = null
        return "Saved $sampleCount samples → HAR_${currentLabel}_*.csv"
    }
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> latestAccel = event.values.copyOf()
            Sensor.TYPE_GYROSCOPE     -> latestGyro  = event.values.copyOf()
            else -> return
        }
        val r = SensorReading(
            ax = latestAccel[0], ay = latestAccel[1], az = latestAccel[2],
            gx = latestGyro[0],  gy = latestGyro[1],  gz = latestGyro[2]
        )
        _liveData.value = LiveSensorData(r.ax, r.ay, r.az, r.gx, r.gy, r.gz)
        window.addLast(r)
        if (window.size > WINDOW_SIZE) window.removeFirst()

        csvWriter?.apply {
            write("${r.timestamp},${r.ax},${r.ay},${r.az},${r.gx},${r.gy},${r.gz},$currentLabel\n")
            sampleCount++
            _sampleCount.value = sampleCount
        }

        if (window.size == WINDOW_SIZE) {
            _features.value = computeFeatures(window.toList())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    private fun computeFeatures(w: List<SensorReading>): WindowFeatures {
        val aMags = w.map { sqrt(it.ax*it.ax + it.ay*it.ay + it.az*it.az) }
        val gMags = w.map { sqrt(it.gx*it.gx + it.gy*it.gy + it.gz*it.gz) }
        val azVals = w.map { it.az }

        fun List<Float>.mean() = sum() / size
        fun List<Float>.std(): Float {
            val m = mean()
            return sqrt(sumOf { ((it - m) * (it - m)).toDouble() }.toFloat() / size)
        }

        val accelMean = aMags.mean()
        val demeaned = aMags.map { it - accelMean }
        var zc = 0
        for (i in 1 until demeaned.size) {
            if (demeaned[i - 1] * demeaned[i] < 0f) zc++
        }
        val stepRate = zc / 2.0f

        return WindowFeatures(
            accelMean   = accelMean,
            accelStd    = aMags.std(),
            accelMax    = aMags.max(),
            gyroMean    = gMags.mean(),
            gyroStd     = gMags.std(),
            gyroMax     = gMags.max(),
            accelZ_mean = azVals.mean(),
            stepRate    = stepRate
        )
    }
}