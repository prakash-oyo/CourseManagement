package com.example.course.controller;

import lombok.*;

import javax.persistence.*;
import java.util.List;



@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "course_program")
public class CourseProgram {
    @Id
    @NonNull
    @Column(name = "course_code")
    String courseCode;

    @NonNull
    @Column(name = "course_name")
    String courseName;

    @NonNull
    @Column(name = "max_credit")
    int maxCredit;

    @ManyToMany
    List<Subject> availableSubjects;

    public CourseProgram(String courseName ,int maxCredit)
    {
        if(SubjectValidator.isCourseName(courseName))
            this.courseName = courseName;
        else this.courseName = "invalid";

        if(maxCredit > 0)
            this.maxCredit = maxCredit;
        else this.maxCredit = -1;

        this.courseCode = SubjectValidator.getSubjectCode(courseName);

        this.availableSubjects = null;

    }

}
