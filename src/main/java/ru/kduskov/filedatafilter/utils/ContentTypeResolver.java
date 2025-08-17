package ru.kduskov.filedatafilter.utils;

import ru.kduskov.filedatafilter.enums.ContentType;

public class ContentTypeResolver {

    public static ContentType resolveType(String input) {
        if(isLong(input))
            return ContentType.LONG;
        if (isFloat(input))
            return ContentType.FLOAT;
        return ContentType.STRING;
    }

    private static boolean isLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private static boolean isFloat(String input) {
        try {
            float f = Float.parseFloat(input);
            return !Float.isNaN(f);
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
