package com.example.course.controller;

import java.util.regex.Pattern;

public class Validator {

    static Boolean isValidName(String name)
    {
        Boolean val = name!=null;

        for(int letter=0 ; letter<name.length() && val ; letter++)
            if(!( name.charAt(letter) == ' ' || Character.isAlphabetic(name.charAt(letter)) ))
                val=false;

            return val;

    }


    static  Boolean isEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        return (email != null&&pat.matcher(email).matches());

    }

    static  Boolean isNumber(String number)
    {
        Boolean val = true;

        if(number.length()!=10||number.charAt(0)<'6'||number.charAt(1)<'1')
            val = false;

        for(int num=2; num<number.length() && val; num++)
            if(number.charAt(num)<'0' && number.charAt(num)>'9')
                val = false;

            return val;
    }




}
