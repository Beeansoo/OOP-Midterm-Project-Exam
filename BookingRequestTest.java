import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingRequestTest {
    public static void main(String[] args) {
        Tutor english = new Tutor("T001", "Ana", Subject.ENGLISH);
        Tutor math = new Tutor("T002", "Ben", Subject.MATH);
        Tutor coding = new Tutor("T003", "Cara", Subject.CODING);

        BookingSystem system = new BookingSystem(english, math, coding);

        system.registerStudent("S001", "Vince");
        system.registerStudent("S002", "Alex");
        system.registerStudent("S003", "Jamie");

        LocalDate date = LocalDate.of(2026, 10, 12);
        LocalDateTime now = LocalDateTime.of(2026, 10, 8, 8, 0);

        TutoringSession firstSession = new TutoringSession(
                "E1", english, date, SessionSlot.MORNING_FIRST,
                new BigDecimal("300.00"));

        TutoringSession secondSession = new TutoringSession(
                "E2", english, date, SessionSlot.MORNING_SECOND,
                new BigDecimal("300.00"));

        system.addSession(firstSession, now);
        system.addSession(secondSession, now);

        check("Empty session has no pending request",
                system.getNextPendingBooking("E1") == null);

        Booking first = system.requestBooking("S001", "E1", now);
        Booking second = system.requestBooking("S002", "E2", now);
        Booking third = system.requestBooking("S003", "E1", now);

        check("Request numbers follow submission order",
                first.getRequestNumber() == 1
                        && second.getRequestNumber() == 2
                        && third.getRequestNumber() == 3);

        check("New requests remain pending",
                first.getStatus() == BookingStatus.PENDING
                        && second.getStatus() == BookingStatus.PENDING
                        && third.getStatus() == BookingStatus.PENDING);

        check("FIFO selects oldest request for first session",
                system.getNextPendingBooking("E1") == first);

        check("Each session has its own FIFO selection",
                system.getNextPendingBooking("E2") == second);

        check("Multiple students can request the same session",
                system.getBookings().size() == 3);

        expectRejected(system, " S001 ", " E1 ", now,
                "Duplicate active request rejected");

        expectRejected(system, "UNKNOWN", "E1", now,
                "Unknown student rejected");

        expectRejected(system, "S001", "UNKNOWN", now,
                "Unknown session rejected");

        expectRejected(system, "S002", "E1", firstSession.getStartTime(),
                "Request at session start rejected");

        Booking fourth = system.requestBooking("S002", "E1", now);

        check("Failed requests do not consume numbers",
                fourth.getRequestNumber() == 4);

        system.getBookings().clear();

        check("Clearing list copy preserves bookings",
                system.getBookings().size() == 4);
    }

    private static void expectRejected(
            BookingSystem system, String studentId, String sessionId,
            LocalDateTime now, String testName) {
        int previousSize = system.getBookings().size();

        try {
            system.requestBooking(studentId, sessionId, now);
            check(testName, false);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            check(testName,
                    system.getBookings().size() == previousSize);
            System.out.println("  Reason: " + exception.getMessage());
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}