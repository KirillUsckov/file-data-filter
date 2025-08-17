package ru.kduskov.filedatafilter.utils;

import ru.kduskov.filedatafilter.enums.ContentType;

public class ContentTypeResolver {

    public static ContentType resolveType(String input) {
        if(isInt(input))
            return ContentType.INT;
        if (isFloat(input))
            return ContentType.FLOAT;
        return ContentType.STRING;
    }

    private static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private static boolean isFloat(String input) {
        try {
            Float.parseFloat(input);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
