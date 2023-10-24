package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderState{
    COMPLETED, ONGOING, CANCELLED
}
