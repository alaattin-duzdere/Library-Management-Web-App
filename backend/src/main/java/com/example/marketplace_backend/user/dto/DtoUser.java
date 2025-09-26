package com.example.marketplace_backend.user.dto;

import com.example.marketplace_backend.common.enums.Role;
import lombok.Data;

@Data
public class DtoUser {

    private String username;

    private String password;

    private Role role ;

}
