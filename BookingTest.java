import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingTest {
    public static void main(String[] args) {
        Student student = new Student("S001", "Vince Lachica");
        Tutor tutor = new Tutor("T001", "Ana", Subject.ENGLISH);

        TutoringSession session = new TutoringSession(
                "S001", tutor, LocalDate.of(2026, 10, 12),
                SessionSlot.MORNING_FIRST,
                new BigDecimal("300.00"));

        LocalDateTime beforeStart =
                LocalDateTime.of(2026, 10, 12, 8, 0);

        Booking first = new Booking(1, student, session);

        check("New booking is pending",
                first.getStatus() == BookingStatus.PENDING);

        check("Booking fee is stored",
                first.getBookingFee().equals(
                        new BigDecimal("300.00")));

        first.approve(beforeStart);

        check("Pending booking can be approved",
                first.getStatus() == BookingStatus.APPROVED);

        Cancellable cancellable = first;
        cancellable.cancel(beforeStart);

        check("Cancellation through interface works",
                first.getStatus() == BookingStatus.CANCELLED);

        check("Approved cancellation charges ten percent",
                first.getCancellationFee().equals(
                        new BigDecimal("30.00")));

        try {
            first.cancel(beforeStart);
            check("Repeated cancellation rejected", false);
        } catch (IllegalStateException exception) {
            check("Repeated cancellation rejected", true);
        }

        check("Repeated attempt preserves cancellation fee",
                first.getCancellationFee().equals(
                        new BigDecimal("30.00")));

        Booking pending = new Booking(2, student, session);
        pending.cancel(beforeStart);

        check("Pending cancellation is free",
                pending.getStatus() == BookingStatus.CANCELLED
                        && pending.getCancellationFee().equals(
                                new BigDecimal("0.00")));

        Booking rejected = new Booking(3, student, session);
        rejected.reject(" Student schedule conflict ");

        check("Rejection stores status and trimmed reason",
                rejected.getStatus() == BookingStatus.REJECTED
                        && rejected.getRejectionReason().equals(
                                "Student schedule conflict"));

        try {
            rejected.approve(beforeStart);
            check("Rejected booking cannot be approved", false);
        } catch (IllegalStateException exception) {
            check("Rejected booking cannot be approved", true);
        }

        Booking boundary = new Booking(4, student, session);
        boundary.approve(beforeStart);

        try {
            boundary.cancel(session.getStartTime());
            check("Cancellation at session start rejected", false);
        } catch (IllegalStateException exception) {
            check("Cancellation at session start rejected", true);
        }

        check("Failed cancellation preserves approved status",
                boundary.getStatus() == BookingStatus.APPROVED
                        && boundary.getCancellationFee().equals(
                                new BigDecimal("0.00")));

        Booking blankReason = new Booking(5, student, session);

        try {
            blankReason.reject("   ");
            check("Blank rejection reason rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank rejection reason rejected", true);
        }

        check("Invalid rejection leaves booking pending",
                blankReason.getStatus() == BookingStatus.PENDING);

        try {
            blankReason.approve(session.getStartTime());
            check("Approval at session start rejected", false);
        } catch (IllegalStateException exception) {
            check("Approval at session start rejected", true);
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}