package com.auth.main;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth.api.RESTAPI;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class UserAuthWebtests extends UserAuthenticationFinalApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getUsers() throws Exception {
        mockMvc.perform(get(RESTAPI.ENDPOINT_USERS))
                .andExpect(status().isOk());
    }

    @Test
    public void successLogin() throws Exception {
        mockMvc.perform(post(RESTAPI.ENDPOINT_USERS_LOGIN)
                        .param("userName", "user-9")
                        .param("password", "user-9-pwd-55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid_user").value(true))
                .andExpect(jsonPath("$.status_code").value(200));

    }

    @Test
    public void failureLogin() throws Exception {
        mockMvc.perform(post(RESTAPI.ENDPOINT_USERS_LOGIN)
                        .param("userName", "user-9-10")
                        .param("password", "user-9-pwd-55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid_user").value(false))
                .andExpect(jsonPath("$.status_code").value(500));
    }

}

