package com.example.course.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @NonNull
    @Column(name = "subject_code")
    String subjectCode;

    @NonNull
    @Column(name = "subject_name")
    String subjectName;

    @NonNull
    @Column(name = "credit")
    int credit;


    public Subject(@NonNull String subjectName, int credit) {

        if(SubjectValidator.isValidSubject(subjectName))
            this.subjectName = subjectName;

        if(credit > 0)
            this.credit = credit;
        else this.credit = -1;

        this.subjectCode= SubjectValidator.getSubjectCode(subjectName);

    }



    @NonNull
    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(@NonNull String subjectCode) {
        this.subjectCode = subjectCode;
    }

    @NonNull
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(@NonNull String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
