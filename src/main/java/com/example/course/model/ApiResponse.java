package com.example.course.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> implements Serializable
{

    T data;
    Boolean status;
    String message;

}
