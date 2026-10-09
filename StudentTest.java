public class StudentTest {
    public static void main(String[] args) {
        Student student = new Student(" S001 ", " Vince Lachica ");

        check("Constructor stores trimmed values",
                student.getId().equals("S001")
                        && student.getName().equals("Vince Lachica"));

        check("Student directory details",
                student.getDirectoryDetails().equals(
                        "Student S001 | Vince Lachica"));

        Person person = student;

        check("Directory method works through Person reference",
                person.getDirectoryDetails().equals(
                        "Student S001 | Vince Lachica"));

        person.setName(" Vince L. ");

        check("Inherited setter updates the same object",
                student.getName().equals("Vince L."));

        check("Directory reflects updated name",
                person.getDirectoryDetails().equals(
                        "Student S001 | Vince L."));

        try {
            student.setName("   ");
            check("Blank name rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank name rejected", true);
        }

        check("Rejected update preserves previous name",
                student.getName().equals("Vince L."));

        try {
            new Student("   ", "Ana");
            check("Blank ID rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank ID rejected", true);
        }

        try {
            new Student(null, "Ana");
            check("Null ID rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null ID rejected", true);
        }

        try {
            new Student("S002", null);
            check("Null name rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null name rejected", true);
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}