import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TutoringSessionTest {
    public static void main(String[] args) {
        Tutor tutor = new Tutor("T001", "Ana", Subject.ENGLISH);
        LocalDate date = LocalDate.of(2026, 10, 12);

        TutoringSession first = new TutoringSession(
                " S001 ", tutor, date, SessionSlot.MORNING_FIRST,
                new BigDecimal("300"));

        check("Session details stored",
                first.getSessionId().equals("S001")
                        && first.getTutor() == tutor
                        && first.getSlot() == SessionSlot.MORNING_FIRST);

        check("Start time calculated",
                first.getStartTime().equals(
                        LocalDateTime.of(2026, 10, 12, 9, 0)));

        check("End time calculated",
                first.getEndTime().equals(
                        LocalDateTime.of(2026, 10, 12, 10, 30)));

        check("Fee stored with two decimal places",
                first.getSessionFee().equals(
                        new BigDecimal("300.00")));

        TutoringSession sameSlot = new TutoringSession(
                "S002", tutor, date, SessionSlot.MORNING_FIRST,
                new BigDecimal("300.00"));

        check("Same slot overlaps",
                first.overlaps(sameSlot));

        TutoringSession second = new TutoringSession(
                "S003", tutor, date, SessionSlot.MORNING_SECOND,
                new BigDecimal("300.00"));

        check("Separate slots do not overlap",
                !first.overlaps(second));

        TutoringSession nextDate = new TutoringSession(
                "S004", tutor, date.plusDays(3),
                SessionSlot.MORNING_FIRST, new BigDecimal("300.00"));

        check("Different dates do not overlap",
                !first.overlaps(nextDate));

        try {
            new TutoringSession(
                    "S005", tutor, null, SessionSlot.AFTERNOON,
                    new BigDecimal("300.00"));
            check("Null date rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null date rejected", true);
        }

        try {
            first.overlaps(null);
            check("Null comparison rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null comparison rejected", true);
        }

        BigDecimal[] invalidFees = {
            null,
            BigDecimal.ZERO,
            new BigDecimal("-100.00"),
            new BigDecimal("300.005")
        };

        String[] testNames = {
            "Null fee rejected",
            "Zero fee rejected",
            "Negative fee rejected",
            "Fraction of a centavo rejected"
        };

        for (int i = 0; i < invalidFees.length; i++) {
            try {
                new TutoringSession(
                        "INVALID" + i, tutor, date,
                        SessionSlot.AFTERNOON, invalidFees[i]);
                check(testNames[i], false);
            } catch (IllegalArgumentException exception) {
                check(testNames[i], true);
            }
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}