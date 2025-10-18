package com.example.library_management.user.dto;

import com.example.library_management.common.enums.Role;
import lombok.Data;

@Data
public class DtoUser {

    private String username;

//    private String password;

    private String email;

    private Role role ;

}
