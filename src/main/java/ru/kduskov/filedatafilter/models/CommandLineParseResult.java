package ru.kduskov.filedatafilter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kduskov.filedatafilter.enums.CommandLineArg;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CommandLineParseResult {
    private final Map<CommandLineArg, String> options;
    private final List<String> files;
    private final List<String> warnings;
    private final List<String> errors;

    public CommandLineParseResult(Map<CommandLineArg, String> options,
                                   List<String> files,
                                   List<String> warnings,
                                   List<String> errors) {
        this.options = Collections.unmodifiableMap(options);
        this.files = Collections.unmodifiableList(files);
        this.warnings = Collections.unmodifiableList(warnings);
        this.errors = Collections.unmodifiableList(errors);
    }
    public Map<CommandLineArg, String> getOptions() { return options; }
    public List<String> getFiles() { return files; }
    public List<String> getWarnings() { return warnings; }
    public List<String> getErrors() { return errors; }

    public boolean hasWarnings() { return !warnings.isEmpty(); }
    public boolean hasErrors()   { return !errors.isEmpty(); }
}
