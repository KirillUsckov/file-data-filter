package ru.kduskov.filedatafilter.models.stat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntStat extends BaseStat {
    private double avg;
    private int min;
    private int max;
    private int sum;
}
