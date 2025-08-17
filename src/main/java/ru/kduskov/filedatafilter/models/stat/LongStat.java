package ru.kduskov.filedatafilter.models.stat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LongStat extends BaseStat {
    private double avg;
    private long min;
    private long max;
    private long sum;
}
