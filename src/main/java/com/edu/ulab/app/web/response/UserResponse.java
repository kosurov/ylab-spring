package com.edu.ulab.app.web.response;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String fullName;
    private String title;
    private int age;
}