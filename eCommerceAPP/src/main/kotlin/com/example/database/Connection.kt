package com.example.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Connection {
        //DB variables
        fun dbConnection(): Connection? {
/*
            val username = "root"
            val password = "tO8ps5KZUS"
            val jdbcUrl = "jdbc:postgresql://95.19.115.169:5432/ecommerceapp"*/
            //Ivan localhost:5433 / Margaux localhost:5432
            val jdbcUrl = "jdbc:postgresql://localhost:5433/ecommerceapp"
            val username = "postgres"
            val password = "postgres"
            var connection :Connection? = null

            //Connection to DB
            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password)
                println("[SUCCESS] Connected to DB ")
            } catch (e: SQLException) {
                println("[ERROR] Can't connect to DB | Error Code:${e.errorCode}: ${e.message} ")
            }
            return connection
        }
}