package com.example.course.controller;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "UserCourseProgram")
public class UserCourseProgram {
    @Id
    @NonNull
    @Column(name="email")
    String email;
    @NonNull
    @Column(name="courseName")
    String courseName;


    @ManyToMany
    List<Subject> addedSubject;

    public UserCourseProgram(@NonNull String email, @NonNull String courseName) {

        Boolean isEmail=Validator.isEmail(email);

        if(isEmail)
            this.email = email;
        else this.email="invalid";

        Boolean isCourseName=Validator_Subject.isCourseName(courseName);

        if(isCourseName)
            this.courseName=courseName;
        else this.courseName="invalid";


        this.addedSubject=null;

    }

    public UserCourseProgram() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<Subject> getAddedSubject() {
        return addedSubject;
    }

    public void setAddedSubject(List<Subject> addedSubject) {
        this.addedSubject = addedSubject;
    }
}

