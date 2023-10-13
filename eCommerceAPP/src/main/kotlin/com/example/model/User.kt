package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var userID: Int? = null,
    var userImage : String?= null,
    var userEmail : String?= null,
    var userPass : String?= null
)


