package com.example.database


import com.example.model.UserInfo

interface UserDao {

    fun getAllUsers(): List<UserInfo>?
    fun getUserById(id: Int): UserInfo?
    fun getUserByEmail(email: String): UserInfo?
    fun addUser(userInfo: UserInfo): Boolean
    fun deleteUser(id: Int): Boolean
    fun updateUserInfo(userInfo: UserInfo) : Boolean
    fun updateUserEmail(oldEmail: String, newEmail: String): Boolean
    fun updateUserPassword(userEmail: String, password: String, salt: String): Boolean
    fun updateUserValidation(userEmail: String, validation: Boolean): Boolean
    fun updateUserPicture(pictureName: String, id: Int) : Boolean
}