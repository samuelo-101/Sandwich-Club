package com.udacity.sandwichclub.utils;

import android.content.Context;

import java.util.List;

public class AppUtils {

    public static String getConcatenatedStringFromListOfStrings(List<String> stringList) {
        if(stringList != null && !stringList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int index = 0; index < stringList.size(); index++) {
                stringBuilder.append(stringList.get(index));
                if(index != stringList.size() - 1) {
                    stringBuilder.append("\n");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    public static String getStringResourceByName(Context context, String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
}
