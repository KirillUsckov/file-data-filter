package ru.kduskov.filedatafilter.parsers;

import ru.kduskov.filedatafilter.enums.FileProcessingStatus;
import ru.kduskov.filedatafilter.utils.ContentTypeResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class FileParser {
    public static FileProcessingStatus parseInLists(String fileName, List<String> strings, List<Long> longs, List<Float> floats) {
        try (BufferedReader br = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            for (String line; (line = br.readLine()) != null; ) {
                switch (ContentTypeResolver.resolveType(line)){
                    case LONG:
                        longs.add(Long.valueOf(line));
                        break;
                    case FLOAT:
                        floats.add(Float.valueOf(line));
                        break;
                    default:
                        strings.add(line);
                }
            }
            return FileProcessingStatus.OK;
        } catch (NoSuchFileException e) {
            return FileProcessingStatus.FILE_IS_MISSING;
        } catch (IOException e) {
            return FileProcessingStatus.FAILED_TO_PROCESS;
        }

    }
}
