package com.edu.ulab.app.web.response;

import lombok.Data;

@Data
public class BookResponse {
    private Integer id;
    private String title;
    private String author;
    private long pageCount;
}