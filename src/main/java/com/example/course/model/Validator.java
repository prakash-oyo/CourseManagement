package com.example.course.model;

import java.util.regex.Pattern;

public class Validator {

    public static Boolean isValidName(String name)
    {
        if(name.isEmpty())
            return false;

        for(int letter=0 ; letter<name.length() ; letter++)
            if(!( name.charAt(letter) == ' ' || Character.isAlphabetic(name.charAt(letter)) ))
                return false;

            return true;

    }

    public static  Boolean isValidEmail(String email)
    {

        if(email.length() <= 0)
            return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        return (email != null && pat.matcher(email).matches());

    }

    public static  Boolean isValidNumber(String number)
    {
        if(number.length()!=10||number.charAt(0)<'6'||number.charAt(1)<'1')
            return false;

        for(int num=2; num < number.length() ; num++)
            if(number.charAt(num)<'0' && number.charAt(num)>'9')
                return false;

            return true;
    }

    public static Boolean isValidRole(String role)
    {
        if(role.compareTo("ADMIN")==0||role.compareTo("STUDENT")==0)
            return true;

        return false;

    }

}
