package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStates{
    COMPLETED, ONGOING, CANCELLED
}
