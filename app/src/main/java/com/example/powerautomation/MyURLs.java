package com.example.powerautomation;

public class MyURLs {
    public final static String loginURL = "http://sgh2020.tonysolutions.co/check_user.php";
    public final static String updateURL = "http://sgh2020.tonysolutions.co/check_user.php?uname=sej@gmail.com&flag=2&s_time=120&w_time=120&pass=tony";

    public static String userLoginURL(String user, String pass){
        String strLoginURL = loginURL + "?" + "uname=" + user + "&pass=" + pass;
        return strLoginURL;
    }

    public static String setOnOff(String flag, String username, String password){
        String startURL = "http://sgh2020.tonysolutions.co/update_board.php" +
                "?uname=" + username +
                "&flag=" + flag +
                "&s_time=" + 0 +
                "&w_time=" + 0 +
                "&pass=" + password;

        return startURL;
    }

    public static String setSchedule(int waitMin, int runMin, String username, String password){
        String scheduleURL = "http://sgh2020.tonysolutions.co/update_board.php";

        if(waitMin == 0){
            return scheduleURL +
                    "?uname=" + username +
                    "&flag=" + 1 +
                    "&s_time=" + runMin +
                    "&w_time=" + 0 +
                    "&pass=" + password;
        } else {
            return scheduleURL +
                    "?uname=" + username +
                    "&flag=" + 0 +
                    "&s_time=" + runMin +
                    "&w_time=" + waitMin +
                    "&pass=" + password;
        }

    }



}
