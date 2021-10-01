package com.auth.main;

import com.auth.api.RESTAPI;
import com.auth.constants.AuthConstants;
import com.auth.dao.UserDAOImpl;
import com.auth.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserAuthentication {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(RESTAPI.ENDPOINT_USERS_WELCOME)
    public String welcome() {
        return "Welcome to User Authentication Application.";
    }

    @PostMapping(RESTAPI.ENDPOINT_USERS_LOGIN)
    public String login(@RequestParam String userName, @RequestParam String password) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);

        UserDAOImpl userDAO = new UserDAOImpl(jdbcTemplate);
        JSONObject response = userDAO.login(user);
        return response.toString();
    }

    @GetMapping(RESTAPI.ENDPOINT_USERS)
    public String getUsers() {
        UserDAOImpl userDAO = new UserDAOImpl(jdbcTemplate);
        List<User> usersData = userDAO.getUsers();

        JSONArray responseData = new JSONArray();
        for (User user : usersData) {
            JSONObject obj = new JSONObject();
            obj.put(AuthConstants.USER_NAME, user.getUserName());
            obj.put(AuthConstants.PASSWORD, user.getPassword());
            responseData.put(obj);
        }

        JSONObject result = new JSONObject();
        result.put(AuthConstants.DATA, responseData);
        return result.toString();
    }

    @PostMapping(RESTAPI.ENDPOINT_USERS_DUMP)
    public String dumpUsersData() {
        UserDAOImpl userDAO = new UserDAOImpl(jdbcTemplate);
        userDAO.insertDumpData();

        JSONObject result = new JSONObject();
        result.put(AuthConstants.MESSAGE, "Inserted Temp Users Data");
        return result.toString();
    }
}
