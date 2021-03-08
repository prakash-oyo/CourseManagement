package com.example.course.model;


public class SubjectValidator {
    public static Boolean isValidSubject(String subject)
    {
        if(subject.isEmpty())
            return false;

        for(int letter=0 ; letter < subject.length(); letter++)
            if(!( subject.charAt(letter) == ' ' || Character.isAlphabetic(subject.charAt(letter)) ))
                return false;

        return true;

    }

    public static Boolean isValidCredit(int credit)
    {
        return Character.isDigit(credit);
    }

    public static String getSubjectCode(String subjectName)
    {
        String subjectCode = subjectName.replaceAll("\\s", ""); ;
        subjectCode = subjectCode + String.valueOf(subjectCode.length());

        return subjectCode;

    }


    public static Boolean isCourseName(String courseName)
    {
        if(courseName.isEmpty())
            return false;

        for(int character = 0 ; character < courseName.length() ; character++)
            if(!( courseName.charAt(character) == ' ' || Character.isAlphabetic(courseName.charAt(character)) ))
                return false;

        return true;
    }
}
