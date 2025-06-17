package com.example

import retrofit2.Call
import retrofit2.http.*

/**
 * API interface for User CRUD operations
 */
interface UserApi {
    @GET("users")
    fun getAllUsers(): Call<List<User>>
    
    @GET("users/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>
    
    @POST("users")
    fun createUser(@Body user: UserCreateRequest): Call<User>
    
    @PUT("users/{id}")
    fun updateUser(@Path("id") id: Long, @Body user: UserUpdateRequest): Call<User>
    
    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>
}

data class UserCreateRequest(val name: String)

data class UserUpdateRequest(val name: String)
