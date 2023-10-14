package com.example.routes


import com.example.database.*
import com.example.model.AuthRequest
import com.example.model.AuthResponse
import com.example.model.User
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.userRoutes(hashingService: HashingService, tokenService: TokenService, tokenConfig: TokenConfig) {

    val dao = UserImpl()

    route("/users") {
        post("/login") {
            val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, "The request is null.")
                return@post
            }

            // Get the user from the BD with the email provided in the request
            val user = dao.getUserByEmail(request.email)

            if (user == null){
                call.respond(HttpStatusCode.NotFound, "Email not found.")
                return@post
            }
            // Verify the pass received with the pass and salt stored in the BD
            val isValidPass = hashingService.verify(
                request.password,
                SaltedHash(user.userPass,user.userSalt))

            if (!isValidPass){
                call.respond(HttpStatusCode.Unauthorized, "Incorrect password")
                return@post
            }
            // Then create a token and return it
            val token = tokenService.generate(tokenConfig,
                TokenClaim("userEmail", user.userEmail)
            )
            call.respond(HttpStatusCode.OK, AuthResponse(token))
        }

        // Creates a new user with an encrypted password
        post {
            val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            // Encrypts the password provided and creates a new user to store in the DB
            val saltedHash = hashingService.generateSaltedHash(request.password)
            val newUser = User(0,
                "placeholder.jpg",
                request.email,
                saltedHash.hash,
                saltedHash.salt)

            val addedNewUser = dao.addUser(newUser)

            if (addedNewUser) {
                return@post call.respondText("[SUCCESS] New user created successfully.", status = HttpStatusCode.Created)
            } else {
                return@post call.respondText("[ERROR] The new user could not be added.", status = HttpStatusCode.BadRequest)
            }
        }

        authenticate {
            get("secret") {
                // hay que mandar Authorization header con Bearer token
                val principal = call.principal<JWTPrincipal>()
                val userEmail = principal?.getClaim("userEmail", String::class)
                call.respond(HttpStatusCode.OK, "$userEmail")
            }
            get {
                val allUsers = dao.getAllUsers()

                allUsers?.let {
                    call.respond(it)
                } ?: call.respondText("[ERROR] There are no users in the database.", status = HttpStatusCode.NotFound)
            }
            get("/{id?}") {
                if (call.parameters["id"].isNullOrBlank())
                    return@get call.respondText(
                        "[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest
                    )

                val id = call.parameters["id"]!!.toInt()
                val userByID = dao.getUserById(id)



                if (userByID != null) {
                    return@get call.respond(userByID)
                } else {
                    return@get call.respondText(
                        "[ERROR] No user with the ID ($id) exists.",
                        status = HttpStatusCode.NotFound
                    )
                }

            }

            delete("/{id?}") {
                if (call.parameters["id"].isNullOrBlank())
                    return@delete call.respondText(
                        "[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest
                    )

                val id = call.parameters["id"]!!.toInt()

                val deleteUser = dao.deleteUser(id)

                if (deleteUser) {
                    return@delete call.respondText(
                        "[SUCCESS] User successfully deleted.",
                        status = HttpStatusCode.Created
                    )
                } else {
                    return@delete call.respondText(
                        "[ERROR] The user could not be deleted.",
                        status = HttpStatusCode.BadRequest
                    )
                }
            }

            put("/{id?}") {
                if (call.parameters["id"].isNullOrBlank())
                    return@put call.respondText(
                        "[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest
                    )

                val id = call.parameters["id"]!!.toInt()
                val user = User(99, "", "", "","")
                val data = call.receiveMultipart()

                data.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "userID" -> user.userID = part.value.toInt()
                                "userEmail" -> user.userEmail = part.value
                                "userPass" -> user.userPass = part.value

                            }
                        }

                        is PartData.FileItem -> {
                            user.userImage =
                                "uploads/" + part.originalFileName as String // A user.image le asignamos en formato string la ruta donde se guardará la imagen

                            val fileBytes =
                                part.streamProvider().readBytes() //LEEMOS LA IMAGEN QUE HA PASADO POR EL POST
                            File(user.userImage.toString()).writeBytes(fileBytes)//GUARDA LA IMAGEN QUE HA PASADO POR EL POST A LA CARPETA "uploads"
                            //EN BASES DE DATOS SOLO GUARDAR URL del archivo
                        }

                        else -> {}
                    }
                }

                val updateUser = dao.updateUser(user, id)

                if (updateUser) {
                    return@put call.respondText(
                        "[SUCCESS] User successfully modified.",
                        status = HttpStatusCode.Created
                    )
                } else {
                    return@put call.respondText(
                        "[ERROR] The user could not be modified.",
                        status = HttpStatusCode.BadRequest
                    )
                }
            }
        }
    }
}

