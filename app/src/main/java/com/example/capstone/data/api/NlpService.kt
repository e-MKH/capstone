package com.example.capstone.data.api

<<<<<<< HEAD
class NlpService {
}
=======
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import java.io.Serializable

data class NlpRequest(val text: String)

data class NlpEntity(
    val name: String,
    val type: String,
    val salience: Double,
    val metadata: Map<String, String>? = null


) : Serializable

data class NlpResponse(
    val difficulty: String?,
    val entities: List<NlpEntity>,
    val text: String = "" // 서버 응답에 따라 없어도 무방하게 기본값 추가
)


interface NlpService {
    @POST("/analyze")
    fun analyzeText(@Body request: NlpRequest): Call<NlpResponse>
}
>>>>>>> 7e1ef8bb2f88217eec3f51966f580b6a5f761385
