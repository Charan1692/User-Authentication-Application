package com.auth.constants;

public class QueryConstants {

    public static final String USERS_INSERT_QUERY = "INSERT INTO Users (user_name, password) VALUES (?, ?)";
    public static final String USERS_SELECT_ALL_QUERY = "SELECT * FROM Users";
    public static final String USERS_SELECT_QUERY = "SELECT * FROM Users where user_name=?";

    public static final String FAILED_INSERT_QUERY = "INSERT INTO FAILURE_LOGS (user_name, reason, device_type) VALUES (?, ?, ?)";

    public static final String SUCCESS_INSERT_QUERY = "INSERT INTO SUCCESS_LOGS (user_name, device_type) VALUES (?, ?)";

}
