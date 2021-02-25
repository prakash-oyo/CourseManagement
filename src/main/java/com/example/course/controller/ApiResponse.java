package com.example.course.controller;


import java.io.Serializable;

public class ApiResponse<T> implements Serializable
{

    T data;

    Boolean status;
    String message;

    public ApiResponse(T data, Boolean status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
