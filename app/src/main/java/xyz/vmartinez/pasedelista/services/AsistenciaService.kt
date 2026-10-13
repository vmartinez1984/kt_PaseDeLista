package xyz.vmartinez.pasedelista.services

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import xyz.vmartinez.pasedelista.models.AsistenciaModel
import xyz.vmartinez.pasedelista.models.InicioDeSesionModel
import xyz.vmartinez.pasedelista.models.RespuestaModel
import xyz.vmartinez.pasedelista.models.TokenModel
import java.io.File

object AsistenciaService {

    var BASE_URL = "https://utilidades.vmartinez84.xyz/"
    private val apiService: ApiService

    var TokenModel : TokenModel? = null

    //contructor
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun registraAsistencia(asistenciaModel: AsistenciaModel): RespuestaModel{
        return RespuestaModel(12, "Asistencia")
    }

    fun subirFoto(byte: Byte){

    }

    fun iniciarSesion(inicioDeSesionModel: InicioDeSesionModel, callback: ApiCallback<TokenModel>){
        val call = apiService.iniciarSesion(inicioDeSesionModel.usuario, inicioDeSesionModel.contrasenia)
        call.enqueue(object : Callback<TokenModel?> {
            override fun onResponse(call: Call<TokenModel?>, response: Response<TokenModel?>) {
                if(response.isSuccessful){
                    TokenModel = response.body()
                    callback.onSuccess(TokenModel as TokenModel)
                }else{
                    val tokenNulo = TokenModel("")
                    callback.onSuccess(tokenNulo)
                }
            }

            override fun onFailure(call: Call<TokenModel?>, t: Throwable) {
                callback.onError(t.message.toString())
            }
        })
    }

//    fun iniciarSesion(correo: String, contrasenia: String, callback: ApiCallback<TokenModel>){
//        val call = apiService.iniciarSesion(correo,contrasenia)
//    }

    fun registrarAsistencia(asistenciaModel: AsistenciaModel, callback: ApiCallback<RespuestaModel>){
        val token = "Bearer " + TokenModel?.token
        val requestFile = asistenciaModel.selfie.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val bodySelfie = MultipartBody.Part.createFormData("Selfie",asistenciaModel.selfie.name, requestFile)
        val call = apiService.registrarAsistencia(token, asistenciaModel.ubicacionGps, bodySelfie)
        call.enqueue(object : Callback<RespuestaModel?> {
            override fun onResponse(
                call: Call<RespuestaModel?>,
                response: Response<RespuestaModel?>
            ) {
                val respuesta : RespuestaModel? = response.body()
                callback.onSuccess(respuesta as RespuestaModel)
            }

            override fun onFailure(call: Call<RespuestaModel?>, t: Throwable) {

            }
        })
    }

    interface ApiService{
        @GET("Usuarios/Sesiones")
        fun iniciarSesion(
            @Query("correo") usuario: String,
            @Query("contrasenia") contrasenia: String
        ): Call<TokenModel>

        @POST("Usuarios/Asistencias")
        @Multipart
        fun registrarAsistencia(
            @Header("Authorization") token: String,
            @Part("Gps") gps: String,
            @Part Selfie: MultipartBody.Part
        ): Call<RespuestaModel>
    }
}