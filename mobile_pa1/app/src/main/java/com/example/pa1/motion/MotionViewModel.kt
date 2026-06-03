package com.example.pa1.motion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.scan

class MotionViewModel(application: Application) : AndroidViewModel(application) {
    val repo = SensorRepository(application)
    val liveData    = repo.liveData
    val features    = repo.features
    val sampleCount = repo.sampleCountFlow
    val activity = repo.features
        .map { f -> if (f != null) HarClassifier.classify(f) else null }
        .scan(Triple("---", "---", 0)) { (confirmed, last, count), newLabel ->
            if (newLabel == null) Triple(confirmed, last, count)
            else if (newLabel == last) {
                val newCount = count + 1
                if (newCount >= 3) Triple(newLabel, newLabel, 0)
                else Triple(confirmed, newLabel, newCount)
            } else {
                Triple(confirmed, newLabel, 1)
            }
        }
        .map { (confirmed, _, _) -> confirmed }
        .stateIn(viewModelScope, SharingStarted.Lazily, "---")
    override fun onCleared() {
        super.onCleared()
        repo.stopListening()
        repo.stopRecording()
    }
}