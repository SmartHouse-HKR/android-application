package se.hkr.smarthouse.api.auth

import androidx.lifecycle.LiveData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import se.hkr.smarthouse.api.auth.network_responses.LoginResponse
import se.hkr.smarthouse.api.auth.network_responses.RegistrationResponse
import se.hkr.smarthouse.util.GenericApiResponse

// Temporary until a real service is built for the smart house (if that is used for authentication)
interface OpenApiAuthService {
    @POST("account/login")
    @FormUrlEncoded
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>
}