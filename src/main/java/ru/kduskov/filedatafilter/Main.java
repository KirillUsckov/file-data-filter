package ru.kduskov.filedatafilter;

import lombok.extern.slf4j.Slf4j;
import ru.kduskov.filedatafilter.enums.*;
import ru.kduskov.filedatafilter.models.CommandLineParseResult;
import ru.kduskov.filedatafilter.models.ResultModel;
import ru.kduskov.filedatafilter.parsers.CommandLineArgsParser;
import ru.kduskov.filedatafilter.parsers.FileParser;
import ru.kduskov.filedatafilter.utils.FileUtils;
import ru.kduskov.filedatafilter.utils.Statistics;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.kduskov.filedatafilter.constants.DefaultFileNames.*;

@Slf4j
public class Main {
    private static final int OK_CODE = 0;
    private static final int SOME_ERRORS_CODE = 1;
    private static final int ERROR_CODE = 2;

    /**
     * Доступные параметры:
     * -o - путь к резудьтатам работы программы
     * -p - префикс для имен выходных файлов
     * -a - опция для задания режима добавления строк в существующие файлы (по-умолчанию файлы перезаписываются)
     * -s - вывод в консоль краткой статистики по каждому типу данных (только количество элементов записанных в файлы)
     * -f - вывод в консоль полной статистики по каждому типу данных:
     *          - для чисел дополнительно содержит min и max, sum и avg
     *          - для строк - дополнительно содержит размер самой короткой строки и самой длинной
     * Например -o /some/path -p result_ задают вывод в файлы
     * /some/path/result_integers.txt, /some/path/result_strings.txt и тд.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.exit(run(args));
    }

    private static int run(String[] args) {
        CommandLineParseResult lineParseResult = CommandLineArgsParser.parse(args);
        lineParseResult.getWarnings().forEach(log::warn);
        lineParseResult.getErrors().forEach(log::error);

        if (lineParseResult.hasErrors()) {
            return ERROR_CODE;
        }

        Map<CommandLineArg, String> options = lineParseResult.getOptions();
        List<String> fileNames = lineParseResult.getFiles();
        ResultModel resultModel = getResultModel(options);

        List<String> strings = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        List<String> missingFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        int resultCode = processFiles(fileNames, strings, longs, floats, missingFiles, failedFiles);
        if (resultCode == ERROR_CODE) {
            return resultCode;
        }

        writeDataInFiles(resultModel, strings, floats, longs);

        analyzeStatistics(resultModel, strings, floats, longs);

        if (!failedFiles.isEmpty())
            return SOME_ERRORS_CODE;
        if (!missingFiles.isEmpty())
            return SOME_ERRORS_CODE;
        return OK_CODE;
    }

    private static int processFiles(List<String> fileNames, List<String> strings, List<Long> longs, List<Float> floats, List<String> missingFiles, List<String> failedFiles) {
        for (String fileName : fileNames) {
            FileProcessingStatus status = FileParser.parseInLists(fileName, strings, longs, floats);
            switch (status) {
                case FILE_IS_MISSING:
                    missingFiles.add(fileName);
                    break;
                case FAILED_TO_PROCESS:
                    failedFiles.add(fileName);
                    break;
                default:
                    log.info("File '{}' parsing done", fileName);
            }
        }

        if (!missingFiles.isEmpty())
            log.warn("Missing files: " + String.join(", ", missingFiles));
        if (!failedFiles.isEmpty())
            log.error("Failed to process: " + String.join(", ", failedFiles));
        if (fileNames.size() == missingFiles.size() + failedFiles.size()) {
            log.error("All files were broken");
            return ERROR_CODE;
        }
        return OK_CODE;
    }

    private static void writeDataInFiles(ResultModel
                                                 resultModel, List<String> strings, List<Float> floats, List<Long> longs) {
        String filesPath = resultModel.getResultPath();
        filesPath = (filesPath == null) ? System.getProperty("user.dir") : filesPath;

        Path path;

        if (!strings.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + STRING_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, strings)) {
                log.error("Strings file wasn't written: " + path);
            }
        }
        if (!longs.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + INT_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, longs)) {
                log.error("Longs file wasn't written: " + path);
            }
        }
        if (!floats.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + FLOAT_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, floats)) {
                log.error("Floats file wasn't written: " + path);
            }
        }
    }

    private static void analyzeStatistics(ResultModel
                                                  resultModel, List<String> strings, List<Float> floats, List<Long> longs) {
        if (resultModel.getReportType() != null) {
            Statistics stats = new Statistics(resultModel.getReportType());
            stats.analyzeStrings(strings);
            stats.analyzeFloat(floats);
            stats.analyzeLongs(longs);
            System.out.println(stats.print());
        }
    }

    private static ResultModel getResultModel(Map<CommandLineArg, String> args) {
        ResultModel r = new ResultModel();
        r.setWriteMode(WriteMode.REWRITE);
        ReportType reportType = null;

        for (CommandLineArg arg : args.keySet()) {
            switch (arg) {
                case RESULT_PATH:
                    r.setResultPath(args.get(arg));
                    break;
                case FILES_PREFIX:
                    r.setFilesPrefix(args.get(arg));
                    break;
                case WRITE_MODE:
                    r.setWriteMode(WriteMode.APPEND);
                    break;
                case SHORT_REPORT:
                    reportType = (reportType == ReportType.FULL) ? ReportType.FULL : ReportType.SHORT;
                    break;
                case FULL_REPORT:
                    reportType = ReportType.FULL;
                    break;
            }
        }
        r.setReportType(reportType);
        return r;
    }
}
