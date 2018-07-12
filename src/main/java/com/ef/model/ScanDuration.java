package com.ef.model;

import java.time.LocalDateTime;
import java.util.function.Function;

public enum ScanDuration {
    HOURLY("hourly", localDateTime -> localDateTime.plusHours(1L) ) ,
    DAILY("daily", localDateTime -> localDateTime.plusDays(1L)) ;

    private final String argumentId;
    private final Function<LocalDateTime, LocalDateTime> endTimeCalculator;

    ScanDuration(String argumentId, Function<LocalDateTime, LocalDateTime> endTimeCalculator) {
        this.argumentId = argumentId;
        this.endTimeCalculator = endTimeCalculator;
    }

    public static ScanDuration fromArgumentId(String argumentId) {
        return HOURLY.argumentId.equals(argumentId) ? HOURLY : DAILY;
    }

    public LocalDateTime calculateEndTime(LocalDateTime beginTime) {
        return endTimeCalculator.apply(beginTime);
    }
}
