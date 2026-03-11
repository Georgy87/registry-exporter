package org.example.util;

import java.time.ZonedDateTime;

public class TimestampUtils {
    /**
     * Возвращает время на начало часа(обнуляет минуты, секунды и наносекунды).
     *
     * @param in - время, из которого необходимо получить начало часа.
     * @return 0 минут, 0 секунд и 0 наносекунд у времени на входе.
     */
    public static ZonedDateTime atStartHour(ZonedDateTime in) {
        return ZonedDateTime.of(in.getYear(), in.getMonthValue(), in.getDayOfMonth(),
                in.getHour(), 0, 0, 0, in.getZone());
    }
}
