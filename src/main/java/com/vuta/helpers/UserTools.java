package com.vuta.helpers;


import com.google.common.base.Strings;
import com.vuta.model.UserModel;


/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class UserTools {
    public static boolean checkRegister (UserModel user) {
        if(user == null) {
            return false;
        }
        if(Strings.isNullOrEmpty(user.getEmail()) || Strings.isNullOrEmpty(user.getName()) ||
                Strings.isNullOrEmpty(user.getLastName()) || Strings.isNullOrEmpty(user.getName()) ||
                Strings.isNullOrEmpty(user.getUserName())) {
            return false;
        }
        return true;
    }

    public static boolean checkLogin (UserModel user) {
        if(user == null) {
            return false;
        }
        if(Strings.isNullOrEmpty(user.getUserName()) || Strings.isNullOrEmpty(user.getPassword())) {
            return false;
        }
        return true;
    }


}
