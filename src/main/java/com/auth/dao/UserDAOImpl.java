package com.auth.dao;

import com.auth.constants.AuthConstants;
import com.auth.constants.QueryConstants;
import com.auth.model.User;
import com.auth.util.AuthUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Component
public class UserDAOImpl implements UserDAO {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        UserDAOImpl.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getUsers() {
        AuthUtils authUtils = new AuthUtils();
        List<User> usersData = UserDAOImpl.jdbcTemplate.query(
                QueryConstants.USERS_SELECT_ALL_QUERY,
                new RowMapper<User>() {
                    public User mapRow(ResultSet result, int rowNum) throws SQLException {
                        User user = new User();
                        user.setUserName(result.getString(AuthConstants.USER_NAME));
                        String decryptPwd = authUtils.decrypt(
                                result.getString(AuthConstants.PASSWORD),
                                AuthUtils.SECRET_KEY);
                        user.setPassword(decryptPwd);
                        return user;
                    }
                });

        return usersData;
    }

    public void insertDumpData() {
        AuthUtils authUtils = new AuthUtils();
        List<User> usersList = authUtils.getDumpUsersData();

        UserDAOImpl.jdbcTemplate.batchUpdate(
                QueryConstants.USERS_INSERT_QUERY,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement pStmt, int j) throws SQLException {
                        User user = usersList.get(j);
                        pStmt.setString(1, user.getUserName());
                        pStmt.setString(2, user.getPassword());
                    }

                    @Override
                    public int getBatchSize() {
                        return usersList.size();
                    }
                });
    }

    public JSONObject login(User currentUser) {
        AuthUtils authUtils = new AuthUtils();

        //Check for valid user from DB
        List<User> userData = UserDAOImpl.jdbcTemplate.query(
                QueryConstants.USERS_SELECT_QUERY,
                new Object[]{currentUser.getUserName()},
                new RowMapper<User>() {
                    public User mapRow(ResultSet result, int rowNum) throws SQLException {
                        User user = new User();
                        user.setUserName(result.getString("user_name"));
                        user.setPassword(result.getString("password"));
                        return user;
                    }
                });

        String response = "Not a Registered User";
        Boolean isValidUser = Boolean.FALSE;
        if (userData.size() > 0) {
            response = "InValid Password";

            // Encrypting the request password
            String encryptedPassword = authUtils.encrypt(currentUser.getPassword(), AuthUtils.SECRET_KEY);

            // Checking password is valid or not
            String actualPassword = userData.get(0).getPassword();
            boolean isValidPwd = actualPassword.equals(encryptedPassword);

            // If valid Generating Session Id
            if (isValidPwd) {
                isValidUser = Boolean.TRUE;
                String sessionID = UUID.randomUUID().toString();
                response = String.format("Valid User & Session Id : %s", sessionID);
            }
        }

        //Log the Login-status in DB
        String device_type = "Web";
        if (!isValidUser) {
            int row = UserDAOImpl.jdbcTemplate.update(
                    QueryConstants.FAILED_INSERT_QUERY,
                    new Object[]{currentUser.getUserName(), response, device_type});
            System.out.println(" Failure Reason Logged :row: " + row);
        } else {
            int row = UserDAOImpl.jdbcTemplate.update(
                    QueryConstants.SUCCESS_INSERT_QUERY,
                    new Object[]{currentUser.getUserName(), device_type});
            System.out.println("SUCCESS Logged :row: " + row);
        }

        JSONObject result = new JSONObject();
        result.put(AuthConstants.MESSAGE, response);
        result.put(AuthConstants.IS_VALID_USER, isValidUser);
        int status_code = HttpStatus.OK.value();
        if (!isValidUser) {
            status_code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        result.put(AuthConstants.STATUS_CODE, status_code);
        return result;
    }

}
