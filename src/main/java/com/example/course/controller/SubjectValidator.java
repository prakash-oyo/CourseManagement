package com.example.course.controller;


public class SubjectValidator {
    static Boolean isValidSubject(String subject)
    {
        if(subject.isEmpty())
            return false;

        for(int letter=0 ; letter < subject.length(); letter++)
            if(!( subject.charAt(letter) == ' ' || Character.isAlphabetic(subject.charAt(letter)) ))
                return false;

        return true;

    }

    static Boolean isValidCredit(int credit)
    {
        return Character.isDigit(credit);
    }


    static String getSubjectCode(String subjectName)
    {
        String ret = subjectName.replaceAll("\\s", ""); ;
        ret = ret + String.valueOf(ret.length());

        return ret;

    }


    static Boolean isCourseName(String courseName)
    {
        if(courseName.isEmpty())
            return false;

        for(int character = 0 ; character < courseName.length() ; character++)
            if(!( courseName.charAt(character) == ' ' || Character.isAlphabetic(courseName.charAt(character)) ))
                return false;

        return true;
    }
}
