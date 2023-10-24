package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
        var idCart: Int,
        var idUser: Int,
        var productList: List<String>
)