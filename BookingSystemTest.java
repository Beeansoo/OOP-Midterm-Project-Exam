import java.util.List;

public class BookingSystemTest {
    public static void main(String[] args) {
        Tutor english = new Tutor("T001", "Ana", Subject.ENGLISH);
        Tutor math = new Tutor("T002", "Ben", Subject.MATH);
        Tutor coding = new Tutor("T003", "Cara", Subject.CODING);

        BookingSystem system = new BookingSystem(
                english, math, coding);

        check("System starts with three tutors",
                system.getPeople().size() == 3);

        Student student = system.registerStudent(
                " S001 ", " Vince Lachica ");

        check("Student registered with trimmed details",
                student.getId().equals("S001")
                        && student.getName().equals("Vince Lachica"));

        check("Directory contains student and three tutors",
                system.getPeople().size() == 4);

        try {
            system.registerStudent(" S001 ", "Another Student");
            check("Duplicate student ID rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Duplicate student ID rejected", true);
        }

        check("Duplicate attempt does not add a student",
                system.getPeople().size() == 4);

        try {
            system.registerStudent("S002", "   ");
            check("Invalid student rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Invalid student rejected", true);
        }

        check("Invalid attempt leaves directory unchanged",
                system.getPeople().size() == 4);

        List<Person> directory = system.getPeople();
        directory.clear();

        check("Clearing directory copy preserves system records",
                system.getPeople().size() == 4);

        try {
            new BookingSystem(math, english, coding);
            check("Incorrect tutor subjects rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Incorrect tutor subjects rejected", true);
        }

        try {
            new BookingSystem(english, math, null);
            check("Missing tutor rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Missing tutor rejected", true);
        }

        try {
            Tutor duplicateId = new Tutor(
                    "T001", "Another Tutor", Subject.MATH);

            new BookingSystem(english, duplicateId, coding);
            check("Duplicate tutor IDs rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Duplicate tutor IDs rejected", true);
        }

        System.out.println("Directory:");

        for (Person person : system.getPeople()) {
            System.out.println(person.getDirectoryDetails());
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}
