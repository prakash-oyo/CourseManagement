package com.example.course.controller;

import java.util.regex.Pattern;

public class Validator {

    static Boolean isValidName(String name)
    {
        if(name.isEmpty())
            return false;

        for(int letter=0 ; letter<name.length() ; letter++)
            if(!( name.charAt(letter) == ' ' || Character.isAlphabetic(name.charAt(letter)) ))
                return false;

            return true;

    }

    static  Boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        return (email != null && pat.matcher(email).matches());

    }

    static  Boolean isValidNumber(String number)
    {
        if(number.length()!=10||number.charAt(0)<'6'||number.charAt(1)<'1')
            return false;

        for(int num=2; num < number.length() ; num++)
            if(number.charAt(num)<'0' && number.charAt(num)>'9')
                return false;

            return true;
    }




}
