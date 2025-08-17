package ru.kduskov.filedatafilter.exceptions;

import static java.lang.String.format;

public class WrongArgumentProvidedException extends RuntimeException {
    public WrongArgumentProvidedException(String arg) {
        super(format("Wrong argument provided: %s", arg));
    }
}
