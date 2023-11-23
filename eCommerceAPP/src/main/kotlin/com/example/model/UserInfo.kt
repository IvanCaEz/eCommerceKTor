package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    var userID: Int,
    var userImage : String,
    var userEmail : String,
    var userValidated: Boolean,
    var userPass : String,
    var userSalt: String
)


