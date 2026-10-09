import java.time.DayOfWeek;

public class SubjectTest {
    public static void main(String[] args) {
        for (Subject subject : Subject.values()) {
            System.out.print(subject + ": ");

            for (DayOfWeek day : DayOfWeek.values()) {
                if (subject.isAllowedOn(day)) {
                    System.out.print(day + " ");
                }
            }

            System.out.println();
        }

        try {
            Subject.ENGLISH.isAllowedOn(null);
            System.out.println("FAIL: null was accepted.");
        } catch (IllegalArgumentException exception) {
            System.out.println("PASS: null was rejected.");
        }
    }
}