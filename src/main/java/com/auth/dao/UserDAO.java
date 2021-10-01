package com.auth.dao;

import com.auth.model.User;
import org.json.JSONObject;

import java.util.List;

public interface UserDAO {

    public List<User> getUsers();

    public void insertDumpData();

    public JSONObject login(User currentUser);

}
