import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Booking implements Cancellable {
    private final int requestNumber;
    private final Student student;
    private final TutoringSession session;
    private final BigDecimal bookingFee;

    private BookingStatus status;
    private BigDecimal cancellationFee;
    private String rejectionReason;

    public Booking(int requestNumber, Student student,
                   TutoringSession session) {
        if (requestNumber <= 0) {
            throw new IllegalArgumentException(
                    "Request number must be positive.");
        }

        if (student == null || session == null) {
            throw new IllegalArgumentException(
                    "Student and session are required.");
        }

        this.requestNumber = requestNumber;
        this.student = student;
        this.session = session;
        this.bookingFee = session.getSessionFee();

        this.status = BookingStatus.PENDING;
        this.cancellationFee = new BigDecimal("0.00");
        this.rejectionReason = "";
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public Student getStudent() {
        return student;
    }

    public TutoringSession getSession() {
        return session;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getBookingFee() {
        return bookingFee;
    }

    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    void approve(LocalDateTime now) {
        requirePending();
        requireBeforeStart(now);
        status = BookingStatus.APPROVED;
    }

    void reject(String reason) {
        requirePending();

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A rejection reason is required.");
        }

        rejectionReason = reason.trim();
        status = BookingStatus.REJECTED;
    }

    @Override
    public void cancel(LocalDateTime now) {
        if (status != BookingStatus.PENDING
                && status != BookingStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only pending or approved bookings can be cancelled.");
        }

        requireBeforeStart(now);

        if (status == BookingStatus.APPROVED) {
            cancellationFee = bookingFee
                    .multiply(new BigDecimal("0.10"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        status = BookingStatus.CANCELLED;
    }

    private void requirePending() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Booking must be pending.");
        }
    }

    private void requireBeforeStart(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException(
                    "Current date and time are required.");
        }

        if (!now.isBefore(session.getStartTime())) {
            throw new IllegalStateException(
                    "This action is not allowed once the session starts.");
        }
    }
}