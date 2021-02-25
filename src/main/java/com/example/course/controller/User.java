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


    @Override
    public String toString()
    {
        return this.role;
    }


    public User(String name,String email,String number,String role)
    {
        Boolean isName = Validator.isName(name);

        if(isName)
            this.name = name;
        else this.name = "invalid";

        Boolean isEmail = Validator.isEmail(email);

        if(isEmail)
            this.email = email;
        else this.email = "invalid";

        Boolean isNumber=Validator.isNumber(number);

        if(isNumber)
            this.number = number;
        else this.number = "invalid";


        role=role.toUpperCase();

        if((role.compareTo("STUDENT")==0 || role.compareTo("ADMIN")==0))
            this.role = role;
        else this.role = "invalid";

        System.out.println("There");
        System.out.println(this.email);

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
