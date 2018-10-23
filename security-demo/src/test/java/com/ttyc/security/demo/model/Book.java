package com.ttyc.security.demo.model;

import lombok.Data;

@Data
public class Book {
    private String category;
    private String author;
    private String title;
    private Double price;
}