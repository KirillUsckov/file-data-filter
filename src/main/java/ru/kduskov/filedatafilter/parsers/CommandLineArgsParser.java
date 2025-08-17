package ru.kduskov.filedatafilter.parsers;

import ru.kduskov.filedatafilter.enums.CommandLineArg;
import ru.kduskov.filedatafilter.models.CommandLineParseResult;
import ru.kduskov.filedatafilter.utils.FileUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static ru.kduskov.filedatafilter.constants.ErrorAndWarningsConstants.*;
import static ru.kduskov.filedatafilter.enums.CommandLineArg.WRONG_COMMAND;

public final class CommandLineArgsParser {
    private static final String ARG_PREFIX = "-";
    private static final String EMPTY = "";

    private CommandLineArgsParser() {}

    public static CommandLineParseResult parse(String[] args) {
        List<String> warnings = new ArrayList<>();
        List<String> errors   = new ArrayList<>();

        if (args == null || args.length == 0) {
            errors.add(NO_ARGUMENTS_PROVIDED);
            return new CommandLineParseResult(new EnumMap<>(CommandLineArg.class),
                    List.of(), warnings, errors);
        }

        Map<CommandLineArg, String> options = new EnumMap<>(CommandLineArg.class);
        List<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith(ARG_PREFIX)) {
                i = parseArgument(i, arg, args, warnings, options);
            } else {
                parseFileArgument(arg, files, warnings);
            }
        }
        if (files.isEmpty()) {
            errors.add(NO_VALID_FILES_PROVIDED);
        }

        return new CommandLineParseResult(options, files, warnings, errors);
    }

    private static int parseArgument(int i, String arg, String[] args, List<String> warnings, Map<CommandLineArg, String> options) {
        CommandLineArg clArg = CommandLineArg.fromString(arg);
        if (clArg == WRONG_COMMAND) {
            warnings.add(format(UNKNOWN_ARG_IGNORED, arg));
            if (i + 1 < args.length && !args[i + 1].startsWith(ARG_PREFIX)) {
                warnings.add(format(VALUE_FOR_UNKNOWN_ARGUMENT_IGNORED, args[i + 1]));
                i++;
            }
            return i;
        }
        if (options.containsKey(clArg)) { // дубликаты — предупреждение, берём последнее значение
            warnings.add(format(DUPLICATED_VALUE_LAST_ONE_APPLIED, arg));
        }
        if (!clArg.isValueRequired()) { // значение не нужно - идем дальше по циклу
            options.put(clArg, EMPTY);
            return i;
        }
        if (i + 1 >= args.length || args[i + 1].startsWith(ARG_PREFIX)) {
            warnings.add(format(NO_VALUE_PROVIDED_FOR_ARG, arg));
            return i;
        }
        options.put(clArg, args[i + 1]);
        i++;
        return i;
    }



    private static void parseFileArgument(String arg, List<String> files, List<String> warnings) {
        if (FileUtils.isValidPath(arg)) {
            files.add(arg);
        } else {
            warnings.add(format(INVALID_FILE_PATH_FILE_IGNORED, arg));
        }
    }
}
