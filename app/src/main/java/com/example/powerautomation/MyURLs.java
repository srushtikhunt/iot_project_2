package com.example.powerautomation;

public class MyURLs {
    public final static String loginURL = "http://sgh2020.tonysolutions.co/check_user.php";
    public final static String updateURL = "http://sgh2020.tonysolutions.co/check_user.php?uname=sej@gmail.com&flag=2&s_time=120&w_time=120&pass=tony";

    public static String userLoginURL(String user, String pass){
        String strLoginURL = loginURL + "?" + "uname=" + user + "&pass=" + pass;
        return strLoginURL;
    }



}
