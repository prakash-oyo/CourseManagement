package com.example.course.controller;


public class Validator_Subject {
    static Boolean isSubject(String subject)
    {
        Boolean val=subject!=null;

        for(int letter=0 ; letter<subject.length() && val ; letter++)
            if(!( subject.charAt(letter) == ' ' || Character.isAlphabetic(subject.charAt(letter)) ))
                val=false;

        return val;

    }

    static Boolean isCredit(int credit)
    {
        return Character.isDigit(credit);
    }


    static String returnSubCode(String subjectName)
    {
        String ret="";

        int size=0,k=-1;

        int intArray[];
        intArray = new int[20];

        for(int letter=0 ; letter<subjectName.length(); letter++)
            if(subjectName.charAt(letter)!=' ') {

                ret = ret + subjectName.charAt(letter);
                size++;

            }else {

                if(size>0)
                    intArray[++k]=size;

                size=0;

            }

        int index=0;

        while(index<=k)
            ret=ret+String.valueOf(intArray[index++]);

        return ret;

    }


    static Boolean isCourseName(String courseName)
    {
        Boolean val=courseName!=null;

        for(int letter=0 ; letter<courseName.length() && val ; letter++)
            if(!( courseName.charAt(letter) == ' ' || Character.isAlphabetic(courseName.charAt(letter)) ))
                val=false;

        return val;
    }
}
