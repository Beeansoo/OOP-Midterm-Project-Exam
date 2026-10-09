import java.time.LocalTime;

public enum SessionSlot {
    MORNING_FIRST(LocalTime.of(9, 0)),
    MORNING_SECOND(LocalTime.of(11, 0)),
    AFTERNOON(LocalTime.of(14, 0));

    private final LocalTime startTime;

    SessionSlot(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
}