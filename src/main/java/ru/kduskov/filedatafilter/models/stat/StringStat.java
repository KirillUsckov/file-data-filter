package ru.kduskov.filedatafilter.models.stat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringStat extends BaseStat {
    private int minLength;
    private int maxLength;

}
