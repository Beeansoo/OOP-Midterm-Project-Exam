import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class TutoringSession {
    private static final int DURATION_MINUTES = 90;

    private final String sessionId;
    private final Tutor tutor;
    private final SessionSlot slot;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BigDecimal sessionFee;

    public TutoringSession(String sessionId, Tutor tutor,
                           LocalDate date, SessionSlot slot,
                           BigDecimal sessionFee) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Session ID must not be blank.");
        }

        if (tutor == null || date == null || slot == null) {
            throw new IllegalArgumentException(
                    "Tutor, date, and slot are required.");
        }

        if (sessionFee == null || sessionFee.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Session fee must be greater than zero.");
        }

        try {
            this.sessionFee = sessionFee.setScale(
                    2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Session fee must not contain fractions of a centavo.");
        }

        this.sessionId = sessionId.trim();
        this.tutor = tutor;
        this.slot = slot;
        this.startTime = date.atTime(slot.getStartTime());
        this.endTime = startTime.plusMinutes(DURATION_MINUTES);
    }

    public String getSessionId() {
        return sessionId;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public SessionSlot getSlot() {
        return slot;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getSessionFee() {
        return sessionFee;
    }

    public boolean overlaps(TutoringSession other) {
        if (other == null) {
            throw new IllegalArgumentException(
                    "Other session must not be null.");
        }

        return startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }
}