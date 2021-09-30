package com.farmmanagementpro.helper;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

public class HelperMethod {
    public static void createErrorToast(Context context,String message){
        TastyToast.makeText(
                context,
                "Please enter register date !",
                TastyToast.LENGTH_LONG,
                TastyToast.ERROR
        );
    }
}
