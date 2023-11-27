package com.example

import com.example.database.Connection
import com.example.plugins.*
import com.example.security.hashing.HashingImpl
import com.example.security.token.TokenConfig
import com.example.security.token.TokenImpl
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 27031, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val tokenService = TokenImpl()

    val secret = "secret"
    val issuer = "http://0.0.0.0:27031"
    val audience = "user"
    val myRealm = "myRealm"

    val tokenConfig = TokenConfig(
        issuer = issuer,
        audience = audience,
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = secret
    )
    val hashingService = HashingImpl()

    configureSerialization()
    //configureDatabases()
    configureHTTP()
    configureSecurity(tokenConfig)
    configureRouting(hashingService, tokenService, tokenConfig)
}
