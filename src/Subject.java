import java.time.DayOfWeek;

public enum Subject {
    ENGLISH,
    MATH,
    CODING;

    public boolean isAllowedOn(DayOfWeek day) {
        if (day == null) {
            throw new IllegalArgumentException("Day must not be null.");
        }

        switch (this) {
            case ENGLISH:
                return day == DayOfWeek.MONDAY
                        || day == DayOfWeek.THURSDAY;

            case MATH:
                return day == DayOfWeek.TUESDAY
                        || day == DayOfWeek.FRIDAY;

            case CODING:
                return day == DayOfWeek.WEDNESDAY
                        || day == DayOfWeek.SATURDAY;

            default:
                return false;
        }
    }
}