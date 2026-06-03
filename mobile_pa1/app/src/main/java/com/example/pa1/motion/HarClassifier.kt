package com.example.pa1.motion

object HarClassifier {
    private const val STILL_STD_MAX  = 0.69f
    private const val STILL_GYRO_MAX = 0.30f
    private const val TURN_GYRO_MIN = 1.0f
    private const val TURN_STD_MAX  = 1.1f
    private const val RUN_STD_MIN  = 7.36f
    private const val RUN_MAX_MIN  = 25.0f
    private const val RUN_STD_SAFE = 8.50f
    private const val STAIRS_DOWN_STD_MIN = 3.35f
    private const val STAIRS_DOWN_STD_MAX = 7.36f
    private const val STAIRS_UP_STD_MIN  = 0.69f
    private const val STAIRS_UP_STD_MAX  = 3.35f
    private const val STAIRS_UP_GYRO_MIN = 0.75f
    private const val STAIRS_UP_MAX_MIN  = 13.3f
    private const val WALK_STD_MIN = 0.69f
    fun classify(f: WindowFeatures): String {

        if (f.accelStd < STILL_STD_MAX && f.gyroMean < STILL_GYRO_MAX) {
            return "STILL"
        }
        if (f.gyroMean >= TURN_GYRO_MIN && f.accelStd < TURN_STD_MAX) {
            return "TURNING"
        }
        if (f.accelStd > RUN_STD_MIN && f.accelMax > RUN_MAX_MIN) {
            return "RUNNING"
        }
        if (f.accelStd > RUN_STD_SAFE) {
            return "RUNNING"
        }
        if (f.accelStd in STAIRS_DOWN_STD_MIN..STAIRS_DOWN_STD_MAX) {
            return "STAIRS DOWN"
        }
        if (f.accelStd in STAIRS_UP_STD_MIN..STAIRS_UP_STD_MAX
            && f.gyroMean >= STAIRS_UP_GYRO_MIN
            && f.accelMax >= STAIRS_UP_MAX_MIN
        ) {
            return "STAIRS UP"
        }
        if (f.accelStd > WALK_STD_MIN) {
            return "WALKING"
        }
        return "STILL"
    }
}