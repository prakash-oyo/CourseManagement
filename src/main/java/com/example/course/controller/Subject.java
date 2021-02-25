package com.example.course.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Subject")
public class Subject {
    @Id
    @NonNull
    @Column(name="subject_Code")
    String subjectCode;

    @NonNull
    @Column(name="subject_Name")
    String subjectName;

    @NonNull
    @Column(name="credit")
    int credit;



    public Subject(@NonNull String subjectName, int credit) {

        Boolean isSubject = validatorSubject.isSubject(subjectName);

        if(isSubject)
            this.subjectName = subjectName;

        if(credit>0)
            this.credit = credit;
        else this.credit = -1;

        this.subjectCode= validatorSubject.returnSubCode(subjectName);



    }

    public Subject() {

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
