package com.example.course.controller;


import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "CourseProgram")
public class CourseProgram {
    @Id
    @NonNull
    @Column(name="courseCode")
    String courseCode;


    @NonNull
    @Column(name="courseName")
    String courseName;

    @NonNull
    @Column(name="maxCredit")
    int maxCredit;

    @ManyToMany
    List<Subject> availableSubjects;

    public CourseProgram(String courseName ,int maxCredit)
    {


        Boolean isCourseName = Validator_Subject.isCourseName(courseName);
        if(isCourseName)
            this.courseName = courseName;
        else this.courseName = "invalid";

        if(maxCredit>0)
            this.maxCredit = maxCredit;
        else this.maxCredit = -1;

        this.courseCode=Validator_Subject.returnSubCode(courseName);

        this.availableSubjects=null;


    }

    public CourseProgram() {

    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getMaxCredit() {
        return maxCredit;
    }

    public void setMaxCredit(int maxCredit) {
        this.maxCredit = maxCredit;
    }

    public List<Subject> getAvailableSubjects() {
        return availableSubjects;
    }

    public void setAvailableSubjects(List<Subject> availableSubjects) {
        this.availableSubjects = availableSubjects;
    }
}
