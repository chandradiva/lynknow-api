package com.lynknow.api.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class LoginRequest {

    private String username;
    private String password;

    @JsonIgnore
    public HashMap<String, String> getMap() {
        HashMap<String, String> maps = new HashMap<>();

        maps.put("username", this.username);
        maps.put("password", this.password);

        return maps;
    }

}
