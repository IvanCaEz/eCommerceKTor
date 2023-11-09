package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val userID: Int,
    val userImage: String,
    val userEmail: String
)