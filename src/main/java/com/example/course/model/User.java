package com.example.course.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Getter
@Setter
@NoArgsConstructor
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

        if(Validator.isValidEmail(email))
            this.email = email;
        else this.email = "invalid";

        if(Validator.isValidNumber(number))
            this.number = number;
        else this.number = "invalid";

        role = role.toUpperCase();

        if((role.compareTo("STUDENT") == 0 || role.compareTo("ADMIN") == 0))
            this.role = role;
        else this.role = "invalid";

    }

}
