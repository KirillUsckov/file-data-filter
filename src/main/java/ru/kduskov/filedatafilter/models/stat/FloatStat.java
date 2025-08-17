package ru.kduskov.filedatafilter.models.stat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloatStat extends BaseStat {
    private double avg;
    private float min;
    private float max;
    private double sum;
}
