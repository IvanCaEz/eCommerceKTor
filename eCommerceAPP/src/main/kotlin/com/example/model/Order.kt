package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
        val idOrder: Int,
        val idUser: Int,
        val productList: List<String>,
        val totalPrice: Double,
        val orderDate: String,
        val state: OrderState
)