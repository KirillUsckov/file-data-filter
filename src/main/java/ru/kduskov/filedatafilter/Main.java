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
     * @param args
     */
    public static void main(String[] args) {
        CommandLineParseResult parsed = CommandLineArgsParser.parse(args);
        parsed.getWarnings().forEach(log::warn);
        parsed.getErrors().forEach(log::error);

        if (parsed.hasErrors()) {
            System.exit(2);
            return;
        }

        Map<CommandLineArg, String> options = parsed.getOptions();
        List<String> fileNames = parsed.getFiles();
        ResultModel resultModel = getResultModel(options);

        List<String> strings = new ArrayList<>();
        List<Integer> ints = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        List<String> missingFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            FileProcessingStatus status = FileParser.parseInLists(fileName, strings, ints, floats);
            if (status == FileProcessingStatus.FILE_IS_MISSING)
                missingFiles.add(fileName);
            else if (status == FileProcessingStatus.FAILED_TO_PROCESS)
                failedFiles.add(fileName);
        }

        if (!missingFiles.isEmpty())
            log.warn("Missing files: " + String.join(", ", missingFiles));
        if (!failedFiles.isEmpty())
            log.error("Failed to process: " + String.join(", ", failedFiles));
        if (fileNames.size() == missingFiles.size() + failedFiles.size()) {
            log.error(" All files were broken");
            System.exit(2);
        }

        writeDataInFiles(resultModel, strings, floats, ints);

        analyzeStatistics(resultModel, strings, floats, ints);

        int exitCode = (!failedFiles.isEmpty()) ? 1 :
                (!missingFiles.isEmpty()) ? 1 : 0;
        System.exit(exitCode);
    }

    private static void writeDataInFiles(ResultModel resultModel, List<String> strings, List<Float> floats, List<Integer> ints) {
        String filesPath = resultModel.getResultPath();
        filesPath = (filesPath == null) ? System.getProperty("user.dir") : filesPath;

        Path path;

        if (!strings.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + STRING_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, strings)) {
                log.error(" Strings file wasn't written: " + path);
            }
        }
        if (!ints.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + INT_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, ints)) {
                log.error(" Ints file wasn't written: " + path);
            }
        }
        if (!floats.isEmpty()) {
            path = Path.of(filesPath, resultModel.getFilesPrefix() + FLOAT_FILE_NAME);
            if (!FileUtils.writeListToFile(resultModel.getWriteMode(), path, floats)) {
                log.error(" Floats file wasn't written: " + path);
            }
        }
    }

    private static void analyzeStatistics(ResultModel resultModel, List<String> strings, List<Float> floats, List<Integer> ints) {
        if (resultModel.getReportType() != null) {
            Statistics stats = Statistics.getInstance(resultModel.getReportType());
            stats.analyzeStrings(strings);
            stats.analyzeFloat(floats);
            stats.analyzeInts(ints);
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
