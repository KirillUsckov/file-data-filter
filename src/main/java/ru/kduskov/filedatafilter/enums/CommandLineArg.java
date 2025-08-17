package ru.kduskov.filedatafilter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum CommandLineArg {
    RESULT_PATH("-o", true),
    FILES_PREFIX("-p", true),
    WRITE_MODE("-a", false),
    SHORT_REPORT("-s", false),
    FULL_REPORT("-f", false),
    WRONG_COMMAND("", false);
    private final String argValue;
    private final boolean valueRequired;

    private static final Map<String, CommandLineArg> VALUES_AND_NAMES =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(CommandLineArg::getArgValue, e -> e));

    public static CommandLineArg fromString(String input) {
        return VALUES_AND_NAMES.getOrDefault(input, WRONG_COMMAND);
    }
}
