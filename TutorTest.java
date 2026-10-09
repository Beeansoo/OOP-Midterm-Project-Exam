public class TutorTest {
    public static void main(String[] args) {
        Tutor tutor = new Tutor(
                " T001 ", " Ana Santos ", Subject.ENGLISH);

        check("Constructor stores trimmed values",
                tutor.getId().equals("T001")
                        && tutor.getName().equals("Ana Santos"));

        check("Assigned subject is stored",
                tutor.getSubject() == Subject.ENGLISH);

        check("Tutor directory includes subject",
                tutor.getDirectoryDetails().equals(
                        "Tutor T001 | Ana Santos | Subject: ENGLISH"));

        Person person = tutor;
        person.setName(" Ana S. ");

        check("Inherited setter updates tutor name",
                tutor.getName().equals("Ana S."));

        check("Directory works through Person reference",
                person.getDirectoryDetails().equals(
                        "Tutor T001 | Ana S. | Subject: ENGLISH"));

        try {
            tutor.setName("   ");
            check("Blank name rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank name rejected", true);
        }

        check("Rejected update preserves previous name",
                tutor.getName().equals("Ana S."));

        try {
            new Tutor("   ", "Ana", Subject.ENGLISH);
            check("Blank ID rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank ID rejected", true);
        }

        try {
            new Tutor(null, "Ana", Subject.ENGLISH);
            check("Null ID rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null ID rejected", true);
        }

        try {
            new Tutor("T002", null, Subject.MATH);
            check("Null name rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null name rejected", true);
        }

        try {
            new Tutor("T003", "Ben", null);
            check("Null subject rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null subject rejected", true);
        }

        Person[] people = {
            new Student("S001", "Vince Lachica"),
            tutor
        };

        System.out.println("Directory demonstration:");

        for (Person entry : people) {
            System.out.println(entry.getDirectoryDetails());
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}