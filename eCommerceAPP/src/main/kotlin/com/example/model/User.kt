package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var userID: Int,
    var userImage : String,
    var userEmail : String,
    var userPass : String,
    var userSalt: String
)


