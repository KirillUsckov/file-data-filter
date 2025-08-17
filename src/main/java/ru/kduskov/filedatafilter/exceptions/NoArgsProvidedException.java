package ru.kduskov.filedatafilter.exceptions;

public class NoArgsProvidedException extends RuntimeException {
    public NoArgsProvidedException() {
        super("No args provided");
    }
}
