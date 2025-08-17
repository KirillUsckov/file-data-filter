package ru.kduskov.filedatafilter.parsers;

import ru.kduskov.filedatafilter.enums.ContentType;
import ru.kduskov.filedatafilter.enums.FileProcessingStatus;
import ru.kduskov.filedatafilter.utils.ContentTypeResolver;

import java.io.*;
import java.util.List;

import static java.lang.String.format;

public class FileParser {
    public static FileProcessingStatus parseInLists(String fileName, List<String> strings, List<Integer> ints, List<Float> floats) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            for (String line; (line = br.readLine()) != null; ) {
                ContentType type = ContentTypeResolver.resolveType(line);
                switch (type){
                    case INT:
                        ints.add(Integer.valueOf(line));
                        break;
                    case FLOAT:
                        floats.add(Float.valueOf(line));
                        break;
                    default:
                        strings.add(line);
                }
            }
            return FileProcessingStatus.OK;
        } catch (FileNotFoundException e) {
            return FileProcessingStatus.FILE_IS_MISSING;
        } catch (IOException e) {
            return FileProcessingStatus.FAILED_TO_PROCESS;
        }

    }
}
