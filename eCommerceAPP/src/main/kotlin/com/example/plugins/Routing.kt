package com.example.plugins

import com.example.database.Connection
import com.example.database.RelationImpl
import com.example.database.UserImpl
import com.example.routes.relationRoutes
import com.example.routes.userRoutes
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting(hashingService: HashingService, tokenService: TokenService, tokenConfig: TokenConfig) {
    install(Resources)
    routing {
        get("/ktor") {
            call.respondText("Ktor funcionant!")
        }

        val connection = Connection.dbConnection()!!
        val userDao = UserImpl(connection)
        val relationDao = RelationImpl(connection)

        userRoutes(hashingService, tokenService, tokenConfig, userDao)
        relationRoutes(userDao, relationDao)
    }
}