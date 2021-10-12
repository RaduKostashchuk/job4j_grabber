package ru.job4j.utils;

import static java.util.Map.entry;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "1"), entry("фев", "2"), entry("мар", "3"),
            entry("апр", "4"), entry("май", "5"), entry("июн", "6"),
            entry("июл", "7"), entry("авг", "8"), entry("сен", "9"),
            entry("окт", "10"), entry("ноя", "11"), entry("дек", "12"));

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime result = null;
        String[] values = parse.split(" ");
        int year, month, day, hour, minutes;
        if (values.length == 4) {
            year = Integer.parseInt("20" + values[2].substring(0, values.length - 2));
            month = Integer.parseInt(MONTHS.get(values[1]));
            day = Integer.parseInt(values[0]);
            hour = Integer.parseInt(values[3].split(":")[0]);
            minutes = Integer.parseInt(values[3].split(":")[1]);
            result = LocalDateTime.of(year, month, day, hour, minutes);
        } else if (values.length == 2) {
            hour = Integer.parseInt(values[1].split(":")[0]);
            minutes = Integer.parseInt(values[1].split(":")[1]);
            if (values[0].equals("вчера,")) {
                result = LocalDateTime.now().minusDays(1)
                        .withHour(hour)
                        .withMinute(minutes)
                        .truncatedTo(ChronoUnit.MINUTES);
            } else if (values[0].equals("сегодня,")) {
                result = LocalDateTime.now()
                        .withHour(hour)
                        .withMinute(minutes)
                        .truncatedTo(ChronoUnit.MINUTES);
            }
        }
        return result;
    }
}
