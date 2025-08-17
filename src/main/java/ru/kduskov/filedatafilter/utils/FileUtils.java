package ru.kduskov.filedatafilter.utils;

import lombok.extern.slf4j.Slf4j;
import ru.kduskov.filedatafilter.enums.WriteMode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Slf4j
public final class FileUtils {
    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    public static <T> boolean writeListToFile(WriteMode writeMode, Path fullPath, List<T> input) {
        OpenOption[] opts = (writeMode == WriteMode.APPEND)
                ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

        try (BufferedWriter w = Files.newBufferedWriter(fullPath, StandardCharsets.UTF_8, opts)) {
            for (T line : input) {
                w.write(String.valueOf(line));
                w.newLine(); // Writes a new line character
            }
            log.info(format("File '%s' was written successfully", fullPath.getFileName()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
