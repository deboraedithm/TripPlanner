package com.rocketseat.tripplanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TripViewModel : ViewModel() {
    var distance by mutableStateOf("")
    var consumption by mutableStateOf("")
    var fuelPrice by mutableStateOf("")

    val totalCost: Double
        get() {
            val d = distance.toDoubleOrNull() ?: 0.0
            val c = consumption.toDoubleOrNull() ?: 0.0
            val p = fuelPrice.toDoubleOrNull() ?: 0.0
            return if (c > 0) (d / c) * p else 0.0
        }

    fun reset() {
        distance = ""
        consumption = ""
        fuelPrice = ""
    }
}
