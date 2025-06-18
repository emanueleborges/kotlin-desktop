package com.example

import retrofit2.Call
import retrofit2.http.*

/**
 * API interface for User CRUD operations
 */
interface UserApi {
    @GET("/api/v1/users")
    fun getAllUsers(): Call<List<User>>
    
    @GET("/api/v1/users/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>
    
    @GET("/api/v1/users/search")
    fun searchUsersByName(@Query("name") name: String): Call<List<User>>
    
    @POST("/api/v1/users")
    fun createUser(@Body user: UserCreateRequest): Call<User>
    
    @PUT("/api/v1/users/{id}")
    fun updateUser(@Path("id") id: Long, @Body user: UserUpdateRequest): Call<User>
    
    @DELETE("/api/v1/users/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>
}
