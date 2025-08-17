package ru.kduskov.filedatafilter.models;

import lombok.Getter;
import lombok.Setter;
import ru.kduskov.filedatafilter.enums.ReportType;
import ru.kduskov.filedatafilter.enums.WriteMode;

@Setter
@Getter
public class ResultModel {
    private ReportType reportType;
    private WriteMode writeMode;
    private String filesPrefix;
    private String resultPath;
}
