package com.example.plugins

import com.example.routes.userRoutes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/ktor") {
            call.respondText("Ktor funcionant!")
        }

        route("/users"){
            userRoutes()
        }
    }
}