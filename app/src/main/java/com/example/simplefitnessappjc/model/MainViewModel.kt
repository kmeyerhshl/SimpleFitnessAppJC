package com.example.simplefitnessappjc.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplefitnessappjc.network.ErrorCodes
import com.example.simplefitnessappjc.network.FitnessApi
import com.example.simplefitnessappjc.network.FitnessData
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel: ViewModel() {
    val TAG = "MainViewModel"

    private var _fitnessData = mutableStateOf(FitnessData(0.0, 0, "", ErrorCodes.NO_ERROR))
    val fitnessData: State<FitnessData> get() = _fitnessData

    private val _pulseDataSet = MutableStateFlow<List<FloatEntry>>(emptyList())
    val pulseDataSet: StateFlow<List<FloatEntry>> get() = _pulseDataSet
    private var counter = 0

    val baseTimestamp = LocalDateTime.now()


    fun getFitnessData() {
        viewModelScope.launch {
            try {
                val jsonString = FitnessApi.retrofitService.getData()
                _fitnessData.value = parseJsonData(jsonString)
            } catch (e: Exception) {
                Log.i(TAG, "Error loading data ${e.message}")
                val errorCode = when (e) {
                    is java.net.UnknownHostException -> ErrorCodes.INTERNET_ERROR
                    is org.json.JSONException -> ErrorCodes.JSON_ERROR
                    else -> ErrorCodes.JSON_ERROR // Fallback für alle anderen Fehler
                }
                _fitnessData.value = FitnessData(0.0, 0, "", errorCode)
            }
        }
    }

    fun clearPulseData() {
        _pulseDataSet.value = emptyList()
        _fitnessData.value = FitnessData(0.0, 0, "", )
        counter = 0
    }


    private fun parseJsonData(jsonString: String): FitnessData {
        if (jsonString.isEmpty()) return FitnessData(errorcode = ErrorCodes.INTERNET_ERROR)
        try {
            val obj = JSONObject(jsonString)

            val timestamp = LocalDateTime.parse(obj.getString("isotimestamp"), DateTimeFormatter.ISO_DATE_TIME)
            Log.i(TAG, "timestamp: $timestamp")
            val timediff = Duration.between(baseTimestamp, timestamp).seconds
            Log.i(TAG, "Zeit: $timediff")
            val newEntry = FloatEntry(timediff.toFloat(), obj.getDouble("puls").toFloat())
            val currentList = _pulseDataSet.value.toMutableList()
            currentList.add(newEntry)

            // Begrenze max. Anzahl angezeigter Werte
            if (currentList.size > 7) {
                currentList.removeAt(0) // Entferne den ältesten Eintrag
            }

            _pulseDataSet.value = currentList.toList()


            return FitnessData(
                fitness = obj.getDouble("fitness"),
                puls = obj.getInt("puls"),
                timestamp = obj.getString("isotimestamp")
            )
        } catch (e: Exception) {
            Log.i(TAG, "Error parsing JSON ${e.message}")
            return FitnessData(errorcode = ErrorCodes.JSON_ERROR)
        }
    }
}