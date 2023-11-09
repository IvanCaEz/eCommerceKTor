package com.example.database

import com.example.model.UserInfo
import java.sql.SQLException

class UserImpl() : UserDao {

    private val connection = Connection.dbConnection()!!

    override fun getAllUsers(): List<UserInfo>? {
        val sentenceSelect = "SELECT * FROM user_info"
        val userInfos = mutableListOf<UserInfo>()

        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val userID = result.getInt(1)
                val userImage = result.getString(2)
                val userEmail = result.getString(3)
                val userPass = result.getString(4)
                val userSalt = result.getString(5)

                //We build a UserInfo object and putting data into the mutableList
                userInfos.add(UserInfo(userID,userImage,userEmail,userPass,userSalt))
            }
            // Close the sentence
            result.close()
            statement.close()

        } catch (e: SQLException) {
            println("[ERROR] Getting all users failing | Error Code:${e.errorCode}: ${e.message}")
        }
            return userInfos.ifEmpty { null }
    }


    override fun getUserById(id: Int): UserInfo? {
        val sentenceSelect = "SELECT * FROM user_info WHERE userID = $id"
        var userInfoByID = UserInfo(99,"","","","")
        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val userID = result.getInt(1)
                val userImage = result.getString(2)
                val userEmail = result.getString(3)
                val userPass = result.getString(4)
                val userSalt = result.getString(5)

                // Fill up with the information of a user searched by ID
                userInfoByID = UserInfo(userID,userImage,userEmail,userPass, userSalt)
            }
            // Close the sentence
            result.close()
            statement.close()
            return userInfoByID

        }catch (e: SQLException){
            println("[ERROR] Getting data from user with ID: $id | Error Code:${e.errorCode}: ${e.message}")
            return null
        }
    }

    override fun getUserByEmail(email: String): UserInfo? {
        val sentenceSelect = "SELECT * FROM user_info WHERE userEmail = '$email'"
        var userInfoByEmail = UserInfo(0,"","","","")
        try {
            val statement = connection.createStatement()
            val result = statement.executeQuery(sentenceSelect)
            while (result.next()) {
                val userID = result.getInt(1)
                val userImage = result.getString(2)
                val userEmail = result.getString(3)
                val userPass = result.getString(4)
                val userSalt = result.getString(5)

                //Fill up with the information of a user searched by email
                userInfoByEmail = UserInfo(userID,userImage,userEmail,userPass, userSalt)
            }
            // Close the sentence
            result.close()
            statement.close()
            return userInfoByEmail

        }catch (e: SQLException){
            println("[ERROR] Getting data from user with email: $email | Error Code:${e.errorCode}: ${e.message}")
            return null
        }
    }

    override fun addUser(userInfo: UserInfo): Boolean {
        val sentenceInsert = "INSERT INTO user_info VALUES" +
                             "(DEFAULT, ?, ?, ?, ?)"

        try {
            val preparedInsert = connection.prepareStatement(sentenceInsert)
            preparedInsert.setString(1, userInfo.userImage)
            preparedInsert.setString(2, userInfo.userEmail)
            preparedInsert.setString(3, userInfo.userPass)
            preparedInsert.setString(4, userInfo.userSalt)

            // Execute the insert
            preparedInsert.executeUpdate()
            // Close the sentence
            preparedInsert.close()

            return true
        }catch (e: SQLException) {
            println("[ERROR] Failed inserting userInfo | Error Code:${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun deleteUser(id: Int): Boolean {
        val sentenceDelete = "DELETE FROM user_info WHERE userID = $id"

        return try {
            val preparedDelete = connection.prepareStatement(sentenceDelete)
            // Execute the delete
            preparedDelete.executeUpdate()
            // Close the sentence
            preparedDelete.close()

            true
        }catch (e: SQLException){
            println("[ERROR] Failed deletting user | Error Code:${e.errorCode}: ${e.message}")
            false
        }
    }

    override fun updateUserInfo(userInfo: UserInfo): Boolean {
        val sentenceUpdate = "UPDATE user_info SET " +
                "userEmail = ?, userPass = ?, userSalt = ?" +
                " WHERE userID = ?"

        try {
            val preparedUpdate = connection.prepareStatement(sentenceUpdate)

            preparedUpdate.setString(1, userInfo.userEmail)
            preparedUpdate.setString(2, userInfo.userPass)
            preparedUpdate.setString(3, userInfo.userSalt)
            preparedUpdate.setInt(4, userInfo.userID)
            // Execute the update
            preparedUpdate.executeUpdate()
            // Close the sentence
            preparedUpdate.close()
            return true
        } catch (e: SQLException) {
            println("[ERROR] Failed updating UserInfo | Error Code:${e.errorCode}: ${e.message}")
            return false
        }
    }

    override fun updateUserPicture(pictureName: String, id: Int): Boolean {
        val sentenceUpdate = "UPDATE user_info SET userImage = ? WHERE userID = $id"

        return try {
            val preparedUpdate = connection.prepareStatement(sentenceUpdate)

            preparedUpdate.setString(1, pictureName)

            // Execute the update
            preparedUpdate.executeUpdate()
            // Close the sentence
            preparedUpdate.close()
            true
        } catch (e: SQLException) {
            println("[ERROR] Failed updating UserInfo | Error Code:${e.errorCode}: ${e.message}")
            false
        }
    }
}
