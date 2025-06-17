package com.example

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCreateRequest(
    val name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserUpdateRequest(
    val name: String
)
