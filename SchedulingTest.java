import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SchedulingTest {
    public static void main(String[] args) {
        Tutor english = new Tutor("T001", "Ana", Subject.ENGLISH);
        Tutor math = new Tutor("T002", "Ben", Subject.MATH);
        Tutor coding = new Tutor("T003", "Cara", Subject.CODING);

        BookingSystem system = new BookingSystem(english, math, coding);

        LocalDate monday = LocalDate.of(2026, 10, 12);
        LocalDateTime now = LocalDateTime.of(2026, 10, 8, 8, 0);

        system.addSession(makeSession(
                "E1", english, monday, SessionSlot.MORNING_FIRST), now);

        check("English Monday session accepted",
                system.getSessions().size() == 1);

        expectRejected(system, makeSession(
                "E2", english, monday.plusDays(1),
                SessionSlot.MORNING_SECOND), now,
                "Wrong subject day rejected");

        expectRejected(system, makeSession(
                "E3", english, monday.minusDays(1),
                SessionSlot.MORNING_FIRST), now,
                "Sunday rejected");

        expectRejected(system, makeSession(
                "E4", english, monday,
                SessionSlot.MORNING_FIRST), now,
                "Occupied tutor slot rejected");

        expectRejected(system, makeSession(
                "E1", english, monday,
                SessionSlot.MORNING_SECOND), now,
                "Duplicate session ID rejected");

        system.addSession(makeSession(
                "E5", english, monday, SessionSlot.MORNING_SECOND), now);

        system.addSession(makeSession(
                "E6", english, monday, SessionSlot.AFTERNOON), now);

        check("Three sessions on one date accepted",
                system.getSessions().size() == 3);

        expectRejected(system, makeSession(
                "E7", english, monday, SessionSlot.AFTERNOON), now,
                "Fourth session rejected");

        system.addSession(makeSession(
                "E8", english, monday.plusDays(3),
                SessionSlot.MORNING_FIRST), now);

        check("Daily count resets for another date",
                system.getSessions().size() == 4);

        system.addSession(makeSession(
                "M1", math, monday.plusDays(1),
                SessionSlot.MORNING_FIRST), now);

        system.addSession(makeSession(
                "C1", coding, monday.plusDays(2),
                SessionSlot.MORNING_FIRST), now);

        check("Math Tuesday and Coding Wednesday accepted",
                system.getSessions().size() == 6);

        TutoringSession future = makeSession(
                "E9", english, monday.plusDays(3),
                SessionSlot.MORNING_SECOND);

        expectRejected(system, future, future.getStartTime(),
                "Session starting now rejected");

        expectRejected(system, future, future.getEndTime(),
                "Past session rejected");

        Tutor unregistered = new Tutor(
                "T001", "Another Ana", Subject.ENGLISH);

        expectRejected(system, makeSession(
                "E10", unregistered, monday.plusDays(3),
                SessionSlot.AFTERNOON), now,
                "Unregistered tutor object rejected");

        system.getSessions().clear();

        check("Clearing schedule copy preserves records",
                system.getSessions().size() == 6);
    }

    private static TutoringSession makeSession(
            String id, Tutor tutor, LocalDate date, SessionSlot slot) {
        return new TutoringSession(
                id, tutor, date, slot, new BigDecimal("300.00"));
    }

    private static void expectRejected(
            BookingSystem system, TutoringSession session,
            LocalDateTime now, String testName) {
        int previousSize = system.getSessions().size();

        try {
            system.addSession(session, now);
            check(testName, false);
        } catch (IllegalArgumentException exception) {
            check(testName,
                    system.getSessions().size() == previousSize);
            System.out.println("  Reason: " + exception.getMessage());
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}