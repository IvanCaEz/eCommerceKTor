package com.example.routes

import com.example.database.RelationImpl
import com.example.database.UserImpl
import com.example.model.Order
import com.example.model.OrderState
import com.example.model.User
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.relationRoutes(hashingService: HashingService, tokenService: TokenService, tokenConfig: TokenConfig) {
    val userDao = UserImpl()
    val relationDao = RelationImpl()

    authenticate {
        route("/relations"){
            // Order route
            route("/order/{userID?}"){
                // Get all orders of user
                get{
                    val userID = call.parameters["userID"]
                    if (userID.isNullOrBlank()) return@get call.respondText("[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest)

                    val orderList = relationDao.getOrders(userID.toInt())

                    return@get call.respond(HttpStatusCode.OK, orderList)
                }
                
                post{
                    val newOrder = call.receiveNullable<Order>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val addOrder = relationDao.addOrder(newOrder)

                    if (addOrder){
                        return@post call.respondText("[SUCCESS] Order created", status = HttpStatusCode.Created)
                    } else {
                        return@post call.respondText("[ERROR] Order couldn't be created", status = HttpStatusCode.BadRequest)
                    }
                }

                put("/{orderID}") {
                    val orderID = call.parameters["orderID"]
                    if (orderID.isNullOrBlank()) return@put call.respondText("[ERROR] No valid ID has been entered.",
                            status = HttpStatusCode.BadRequest)

                    val newState = call.receiveNullable<OrderState>() ?: kotlin.run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }
                    val putOrder = relationDao.putOrder(orderID.toInt(), newState)
                    if (putOrder){
                        return@put call.respondText("[SUCCESS] Order updated to $newState", status = HttpStatusCode.Created)
                    } else {
                        return@put call.respondText("[ERROR] Order couldn't be updated", status = HttpStatusCode.BadRequest)
                    }
                }
            }

            route("/cart"){
                get("/{userID}"){

                }
            }
            

        }
    }
    
}