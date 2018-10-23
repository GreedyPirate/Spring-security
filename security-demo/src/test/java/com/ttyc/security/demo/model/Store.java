package com.ttyc.security.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Store {
    private List<Book> book;
    private Bicycle bicycle;
}