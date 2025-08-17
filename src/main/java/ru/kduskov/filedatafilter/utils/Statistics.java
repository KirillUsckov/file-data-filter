package ru.kduskov.filedatafilter.utils;

import ru.kduskov.filedatafilter.enums.ContentType;
import ru.kduskov.filedatafilter.enums.ReportType;
import ru.kduskov.filedatafilter.models.stat.FloatStat;
import ru.kduskov.filedatafilter.models.stat.IntStat;
import ru.kduskov.filedatafilter.models.stat.StringStat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;

public class Statistics {
    private static Statistics instance;
    private ReportType reportType;
    private StringStat stringStat;
    private IntStat intStat;
    private FloatStat floatStat;

    private Statistics(ReportType reportType) {
        this.reportType = reportType;
        this.stringStat = new StringStat();
        this.intStat = new IntStat();
        this.floatStat = new FloatStat();
    }

    public static Statistics getInstance(ReportType reportType) {
        if(instance == null) {
            instance = new Statistics(reportType);
        }
        return instance;
    }

    public static Statistics getInstance() {
        if (instance == null)
            instance = new Statistics(ReportType.SHORT);
        return instance;
    }

    public void analyzeStrings(List<String> strings) {
        this.stringStat.setAmount(strings.size());
        if (reportType == ReportType.FULL) {
            this.stringStat.setMinLength(strings.stream().min(Comparator.comparing(String::length)).orElseThrow().length());
            this.stringStat.setMaxLength(strings.stream().max(Comparator.comparing(String::length)).orElseThrow().length());
        }
    }

    public void analyzeInts(List<Integer> ints) {
        this.intStat.setAmount(ints.size());
        if (reportType == ReportType.FULL) {
            this.intStat.setMin(ints.stream().mapToInt(Integer::valueOf).min().orElseThrow());
            this.intStat.setMax(ints.stream().mapToInt(Integer::valueOf).max().orElseThrow());
            this.intStat.setSum(ints.stream().mapToInt(Integer::valueOf).sum());
            this.intStat.setAvg(ints.stream().mapToInt(Integer::valueOf).average().orElseThrow());
        }
    }

    public void analyzeFloat(List<Float> floats) {
        this.floatStat.setAmount(floats.size());
        if (reportType == ReportType.FULL) {
            this.floatStat.setMin(floats.stream().min(Comparator.naturalOrder()).orElseThrow());
            this.floatStat.setMax(floats.stream().max(Comparator.naturalOrder()).orElseThrow());
            this.floatStat.setSum(floats.stream().mapToDouble(Float::floatValue).sum());
            this.floatStat.setAvg(floats.stream().mapToDouble(Float::floatValue).average().orElseThrow());
        }
    }

    public String print() {
        if(reportType == ReportType.SHORT){
            return format(
                    "Strings count : %s%nInts count: %s%nFloats count: %s",
                    stringStat.getAmount(),
                    intStat.getAmount(),
                    floatStat.getAmount());
        } else {
            return format("Strings data:%n\tcount: %s, min length: %s, max length: %s%n" +
                    "Ints data:%n\tcount: %s, min value: %s, max value: %s, avg value: %s, sum: %s%n" +
                    "Floats data:%n\tcount: %s, min value: %s, max value: %s, avg value: %s, sum: %s",
                    stringStat.getAmount(), stringStat.getMinLength(), stringStat.getMaxLength(),
                    intStat.getAmount(), intStat.getMin(), intStat.getMax(), intStat.getAvg(), intStat.getSum(),
                    floatStat.getAmount(), floatStat.getMin(), floatStat.getMax(), floatStat.getAvg(), floatStat.getSum());
        }
    }
}
