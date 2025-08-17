package ru.kduskov.filedatafilter.utils;

import ru.kduskov.filedatafilter.enums.ReportType;
import ru.kduskov.filedatafilter.models.stat.FloatStat;
import ru.kduskov.filedatafilter.models.stat.StringStat;
import ru.kduskov.filedatafilter.models.stat.LongStat;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;

public class Statistics {
    private final ReportType reportType;
    private final StringStat stringStat;
    private final LongStat longStat;
    private final FloatStat floatStat;

    public Statistics(ReportType reportType) {
        this.reportType = reportType;
        this.stringStat = new StringStat();
        this.longStat = new LongStat();
        this.floatStat = new FloatStat();
    }

    public void analyzeStrings(List<String> strings) {
        this.stringStat.setAmount(strings.size());
        if (reportType == ReportType.FULL && !strings.isEmpty()) {
            this.stringStat.setMinLength(strings.stream().min(Comparator.comparing(String::length)).orElseThrow().length());
            this.stringStat.setMaxLength(strings.stream().max(Comparator.comparing(String::length)).orElseThrow().length());
        }
    }

    public void analyzeLongs(List<Long> longs) {
        this.longStat.setAmount(longs.size());
        if (reportType == ReportType.FULL && !longs.isEmpty()) {
            this.longStat.setMin(longs.stream().mapToLong(Long::valueOf).min().orElseThrow());
            this.longStat.setMax(longs.stream().mapToLong(Long::valueOf).max().orElseThrow());
            this.longStat.setSum(longs.stream().mapToLong(Long::valueOf).sum());
            this.longStat.setAvg(longs.stream().mapToLong(Long::valueOf).average().orElseThrow());
        }
    }

    public void analyzeFloat(List<Float> floats) {
        this.floatStat.setAmount(floats.size());
        if (reportType == ReportType.FULL && !floats.isEmpty()) {
            this.floatStat.setMin(floats.stream().min(Comparator.naturalOrder()).orElseThrow());
            this.floatStat.setMax(floats.stream().max(Comparator.naturalOrder()).orElseThrow());
            this.floatStat.setSum(floats.stream().mapToDouble(Float::floatValue).sum());
            this.floatStat.setAvg(floats.stream().mapToDouble(Float::floatValue).average().orElseThrow());
        }
    }

    public String print() {
        if(reportType == ReportType.SHORT){
            return format(
                    "Strings count : %s%nLongs count: %s%nFloats count: %s",
                    stringStat.getAmount(),
                    longStat.getAmount(),
                    floatStat.getAmount());
        } else {
            return format("Strings data:%n\tcount: %s, min length: %s, max length: %s%n" +
                    "Longs data:%n\tcount: %s, min value: %s, max value: %s, avg value: %s, sum: %s%n" +
                    "Floats data:%n\tcount: %s, min value: %s, max value: %s, avg value: %s, sum: %s",
                    stringStat.getAmount(), stringStat.getMinLength(), stringStat.getMaxLength(),
                    longStat.getAmount(), longStat.getMin(), longStat.getMax(), longStat.getAvg(), longStat.getSum(),
                    floatStat.getAmount(), floatStat.getMin(), floatStat.getMax(), floatStat.getAvg(), floatStat.getSum());
        }
    }
}
