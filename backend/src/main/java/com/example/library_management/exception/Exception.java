package com.example.library_management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exception <E>{

    private String path;

    private Date createTime;

    private String hostname;

    private E message;
}
