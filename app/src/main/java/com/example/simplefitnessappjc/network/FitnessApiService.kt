package com.example.simplefitnessappjc.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

object ErrorCodes {
    const val NO_ERROR = 0
    const val INTERNET_ERROR = 1
    const val JSON_ERROR = 2
}

// Hier Basis URL der Webseite, von der Daten abgerufen werden sollen
private const val BASE_URL = "https://us-central1-si-hshl.cloudfunctions.net/"

// Retrofit vorbereiten
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

// Im Interface werden Funktionen definiert, mit denen auf Webseiten der
// BASE_URL Domain zugegriffen werden kann.
// Das Keyword suspend erlaubt, dass diese Funktion später parallel
// im Hintergrund ausgeführt wird.
interface FitnessApiService {
    @GET("testdata_json")
    suspend fun getData(): String
}

// Über diese API (Application Programming Interface)
// können wir in der App Retrofit nutzen.
object FitnessApi {
    val retrofitService : FitnessApiService by lazy { retrofit.create(FitnessApiService::class.java) }
}

data class FitnessData(val fitness: Double  = 0.0,
                       val puls: Int = 0,
                       val timestamp: String = "",
                       val errorcode: Int = ErrorCodes.NO_ERROR
)
