package ru.kduskov.filedatafilter.exceptions;

import static java.lang.String.format;

public class NoRequiredValueForArgumentException extends IllegalArgumentException {
    public NoRequiredValueForArgumentException(String arg) {
        super(format("No value provided for argument %s", arg));
    }
}
