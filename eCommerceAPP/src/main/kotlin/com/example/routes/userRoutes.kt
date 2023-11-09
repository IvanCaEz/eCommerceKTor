package com.example.routes
import com.example.database.*
import com.example.model.AuthRequest
import com.example.model.AuthResponse
import com.example.model.User
import com.example.model.UserInfo
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
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
        post("/signup") {
            val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            // Encrypts the password provided and creates a new user to store in the DB
            val saltedHash = hashingService.generateSaltedHash(request.password)
            val newUserInfo = UserInfo(0,
                "placeholder.png",
                request.email,
                saltedHash.hash,
                saltedHash.salt)

            val addedNewUser = dao.addUser(newUserInfo)

            if (addedNewUser) {
                return@post call.respondText("[SUCCESS] New user created successfully.", status = HttpStatusCode.Created)
            } else {
                return@post call.respondText("[ERROR] The new user could not be added.", status = HttpStatusCode.BadRequest)
            }
        }

        authenticate {
            get("loggedUser") {
                // hay que mandar Authorization header con Bearer token
               call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let {mail->
                  dao.getUserByEmail(mail)?.let { userInfo->
                       val user = User(userInfo.userID, userInfo.userImage, userInfo.userEmail )
                       return@get call.respond(HttpStatusCode.OK, user)
                   }
               }
                return@get call.respond(HttpStatusCode.NotFound)
            }
            get {
                val allUsers = dao.getAllUsers()

                allUsers?.let {
                    call.respond(it)
                } ?: call.respondText("[ERROR] There are no users in the database.", status = HttpStatusCode.NotFound)
            }

            get("{id}/picture"){
                var userImage: File = File("")
                val userID = call.parameters["id"]
                if (userID.isNullOrBlank()) return@get call.respondText("[ERROR] No valid ID has been entered.",
                    status = HttpStatusCode.BadRequest)

                val userPhoto = dao.getUserById(userID.toInt())!!.userImage
                userImage = File("eCommerceAPP/src/main/kotlin/com/example/uploads/$userPhoto")
                println(userPhoto)
                println(userImage.exists())
                if (userImage.exists()){
                    println(userPhoto)
                    call.respondFile(userImage)
                } else {
                    call.respondText("[ERROR] No image found.", status = HttpStatusCode.NotFound)
                }
            }

            delete("/{id?}") {
                if (call.parameters["id"].isNullOrBlank())
                    return@delete call.respondText(
                        "[ERROR] No valid ID has been entered.",
                        status = HttpStatusCode.BadRequest
                    )
                call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let {mail->
                    dao.getUserByEmail(mail)?.let { userInfo->

                        val id = call.parameters["id"]!!.toInt()

                        if (userInfo.userID != id) {
                            return@delete call.respondText(
                                "[ERROR] You can't delete other users.",
                                status = HttpStatusCode.Unauthorized)
                        }

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
                }


            }

            put("/edit") {
                // Update user info in db where mail = (mail from token)
                val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
                    return@put call.respond(HttpStatusCode.BadRequest)
                }
                call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let {mail->
                    dao.getUserByEmail(mail)?.let { user->
                        val saltedHash = hashingService.generateSaltedHash(request.password)
                        val updatedUserInfo = UserInfo(user.userID,
                            user.userImage,
                            request.email,
                            saltedHash.hash,
                            saltedHash.salt)
                        println(updatedUserInfo)
                        val updateUser = dao.updateUserInfo(updatedUserInfo)
                        return@put call.respond(HttpStatusCode.OK, updateUser)
                    }
                }
            }

            put("/picture") {
                // Update user picture in db where mail = (mail from token)
                var userID = 0
                call.principal<JWTPrincipal>()?.getClaim("userEmail", String::class)?.let {mail->
                    userID = dao.getUserByEmail(mail)!!.userID
                }
                val data = call.receiveMultipart()
                var userImage = ""

                data.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> Unit
                        is PartData.FileItem -> {

                             userImage = part.originalFileName as String // A user.image le asignamos en formato string la ruta donde se guardará la imagen

                            val fileBytes =
                                part.streamProvider().readBytes() //LEEMOS LA IMAGEN QUE HA PASADO POR EL POST
                            File("eCommerceAPP/src/main/kotlin/com/example/uploads/$userImage").writeBytes(fileBytes)//GUARDA LA IMAGEN QUE HA PASADO POR EL POST A LA CARPETA "uploads"
                            //EN BASES DE DATOS SOLO GUARDAR URL del archivo
                        }

                        else -> {Unit}
                    }
                }

                val updatePicture = dao.updateUserPicture(userImage, userID)

                if (updatePicture) {
                    return@put call.respondText(
                "[SUCCESS] Picture of user $userID successfully modified.",
                        status = HttpStatusCode.OK
                    )
                } else {
                    return@put call.respondText(
                        "[ERROR] The picture could not be modified.",
                        status = HttpStatusCode.BadRequest
                    )
                }
            }
        }
    }
}
