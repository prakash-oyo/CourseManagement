package com.example.course.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.List;



@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_course_program")
public class UserCourseProgram {
    @Id
    @NonNull
    @Column(name = "email")
    String email;

    @NonNull
    @Column(name = "course_name")
    String courseName;

    @ManyToMany
    List<Subject> addedSubject;

    public UserCourseProgram(@NonNull String email, @NonNull String courseName) {

        Boolean isEmail = Validator.isValidEmail(email);

        if(isEmail)
            this.email = email;
        else this.email = "invalid";

        Boolean isValidCourseName = SubjectValidator.isCourseName(courseName);

        if(isValidCourseName)
            this.courseName = courseName;
        else this.courseName = "invalid";

        this.addedSubject = null;

    }

}

