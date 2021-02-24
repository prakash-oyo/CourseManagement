package com.example.course.controller;


import java.io.Serializable;

public class ApiResponse<T> implements Serializable
{

    T data;

    Boolean successful;
    String message;

    public ApiResponse(T data, Boolean successful, String message) {
        this.data = data;
        this.successful = successful;
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
