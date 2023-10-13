package com.example.dao

import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

interface ADao {

    companion object {
        //DB variables
        val username = "root"
        val password = "tO8ps5KZUS"
        val jdbcUrl = "jdbc:postgresql://95.19.115.169:5432/ecommerceapp"
        var connection = createConnection()

        fun createConnection(): Connection? {
            //Connection to DB
            try {
                println("[SUCCES] Connected to DB ")
                return DriverManager.getConnection(jdbcUrl, username, password)
            } catch (e: SQLException) {
                println("[ERROR] Can't connect to DB | Error Code:${e.errorCode}: ${e.message} ")
                return null;
            }
        }
    }
}