import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingWorkflowTest {
    public static void main(String[] args) {
        Tutor english = new Tutor("T001", "Hiro", Subject.ENGLISH);
        Tutor math = new Tutor("T002", "Vito", Subject.MATH);
        Tutor coding = new Tutor("T003", "Hadji", Subject.CODING);

        BookingSystem system = new BookingSystem(english, math, coding);

        system.registerStudent("S001", "Nathan");
        system.registerStudent("S002", "Kerby");
        system.registerStudent("S003", "Kenshin");

        LocalDateTime now = LocalDateTime.of(2026, 10, 8, 8, 0);

        TutoringSession session = new TutoringSession(
                "E1", english, LocalDate.of(2026, 10, 12),
                SessionSlot.MORNING_FIRST, new BigDecimal("300.00"));

        system.addSession(session, now);

        Booking first = system.requestBooking("S001", "E1", now);
        Booking second = system.requestBooking("S002", "E1", now);
        Booking third = system.requestBooking("S003", "E1", now);

        check("Pending requests do not reserve the place",
                system.isSessionAvailable("E1"));

        try {
            system.approveNextBooking("T002", "E1", now);
            check("Wrong tutor cannot approve", false);
        } catch (IllegalArgumentException exception) {
            check("Wrong tutor cannot approve",
                    first.getStatus() == BookingStatus.PENDING);
        }

        System.out.println(system.approveNextBooking("T001", "E1", now));

        check("FIFO approves first request",
                first.getStatus() == BookingStatus.APPROVED
                        && second.getStatus() == BookingStatus.PENDING);

        check("Approved booking reserves the place",
                !system.isSessionAvailable("E1"));

        System.out.println(system.approveNextBooking("T001", "E1", now));

        check("Full session leaves later requests pending",
                second.getStatus() == BookingStatus.PENDING
                        && third.getStatus() == BookingStatus.PENDING);

        system.cancelBooking(first.getRequestNumber(), now);

        check("Approved cancellation records ten percent",
                first.getStatus() == BookingStatus.CANCELLED
                        && first.getCancellationFee().equals(
                                new BigDecimal("30.00")));

        check("Cancellation releases the place",
                system.isSessionAvailable("E1"));

        check("Cancellation preserves the scheduled session",
                system.getSessions().size() == 1);

        System.out.println(system.approveNextBooking("T001", "E1", now));

        check("Next request approved after cancellation",
                second.getStatus() == BookingStatus.APPROVED
                        && third.getStatus() == BookingStatus.PENDING);

        try {
            system.rejectNextBooking("T001", "E1", "   ");
            check("Blank rejection reason rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank rejection reason rejected",
                    third.getStatus() == BookingStatus.PENDING);
        }

        system.rejectNextBooking(
                "T001", "E1", "Requested lesson is outside tutor expertise.");

        check("Rejection records a reason",
                third.getStatus() == BookingStatus.REJECTED
                        && !third.getRejectionReason().isEmpty());

        Booking renewed = system.requestBooking("S003", "E1", now);

        check("New request after rejection gets a new number",
                renewed.getRequestNumber() == 4);

        system.cancelBooking(renewed.getRequestNumber(), now);

        check("Pending cancellation has no fee",
                renewed.getStatus() == BookingStatus.CANCELLED
                        && renewed.getCancellationFee().equals(
                                new BigDecimal("0.00")));

        try {
            system.cancelBooking(999, now);
            check("Unknown booking rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Unknown booking rejected", true);
        }

        try {
            system.cancelBooking(
                    second.getRequestNumber(), session.getStartTime());
            check("Cancellation at start rejected", false);
        } catch (IllegalStateException exception) {
            check("Cancellation at start rejected",
                    second.getStatus() == BookingStatus.APPROVED);
        }

        check("No pending requests remain",
                system.getNextPendingBooking("E1") == null);
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}