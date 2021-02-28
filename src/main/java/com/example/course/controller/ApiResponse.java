package com.example.course.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@AllArgsConstructor
@Data
@Getter
@Setter
public class ApiResponse<T> implements Serializable
{

    T data;
    Boolean status;
    String message;

}
