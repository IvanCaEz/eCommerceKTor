CREATE DATABASE ecommerceapp;
\c ecommerceapp
CREATE TABLE IF NOT EXISTS user_info (
  userID SERIAL PRIMARY KEY,
  userImage VARCHAR(255),
  userEmail VARCHAR(255) NOT NULL,
  userPass VARCHAR(255) NOT NULL,
  userSalt VARCHAR(255) NOT NULL
);