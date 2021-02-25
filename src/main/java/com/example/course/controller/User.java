package com.example.course.controller;


import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="user_entity")
public class User {
    @NonNull
    @Column(name = "name")
    String name;
    @NonNull
    @Id
    String email;
    @NonNull
    @Column(name = "number")
    String number;
    @NonNull
    @Column(name = "role")
    String role;



    public User(String name,String email,String number,String role)
    {

        if(Validator.isValidName(name))
            this.name = name;
        else this.name = "invalid";

        if(Validator.isEmail(email))
            this.email = email;
        else this.email = "invalid";


        if(Validator.isNumber(number))
            this.number = number;
        else this.number = "invalid";

        role=role.toUpperCase();

        if((role.compareTo("STUDENT") == 0 || role.compareTo("ADMIN") == 0))
            this.role = role;
        else this.role = "invalid";


    }

    public User() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
