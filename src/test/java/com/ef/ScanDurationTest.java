package com.ef;

import com.ef.model.ScanDuration;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ScanDurationTest {


    @Test
    public void testCalculateEndTimeHourly() {
        // yyyy-MM-dd.HH:mm:ss
        LocalDateTime time = LocalDateTime.from(Parser.DATE_TIME_FORMATTER.parse("2018-01-01.14:00:00"));

        LocalDateTime localDateTime = ScanDuration.HOURLY.calculateEndTime(time);

        assertEquals(15, localDateTime.getHour());
    }


    @Test
    public void testCalculateEndTimeDaily() {
        // yyyy-MM-dd.HH:mm:ss
        LocalDateTime time = LocalDateTime.from(Parser.DATE_TIME_FORMATTER.parse("2018-01-01.14:00:00"));

        LocalDateTime localDateTime = ScanDuration.DAILY.calculateEndTime(time);

        assertEquals(2, localDateTime.getDayOfMonth());
    }
}