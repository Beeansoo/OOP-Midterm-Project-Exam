public class PersonTest {
    private static class TestPerson extends Person {
        TestPerson(String id, String name) {
            super(id, name);
        }

        @Override
        public String getDirectoryDetails() {
            return getId() + " | " + getName();
        }
    }

    public static void main(String[] args) {
        Person person = new TestPerson(" P001 ", " Vince Lachica ");

        check("ID is trimmed",
                person.getId().equals("P001"));

        check("Name is trimmed",
                person.getName().equals("Vince Lachica"));

        check("Overridden directory method works",
                person.getDirectoryDetails().equals(
                        "P001 | Vince Lachica"));

        person.setName(" Vince L. ");

        check("Valid name update",
                person.getName().equals("Vince L."));

        try {
            person.setName("   ");
            check("Blank name update rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Blank name update rejected", true);
        }

        check("Rejected update preserves name",
                person.getName().equals("Vince L."));

        try {
            person.setName(null);
            check("Null name update rejected", false);
        } catch (IllegalArgumentException exception) {
            check("Null name update rejected", true);
        }

        String[][] invalidInputs = {
            {"", "Ana"},
            {"   ", "Ana"},
            {null, "Ana"},
            {"P002", ""},
            {"P002", "   "},
            {"P002", null}
        };

        for (int i = 0; i < invalidInputs.length; i++) {
            String testName = "Invalid constructor input " + (i + 1);

            try {
                new TestPerson(
                        invalidInputs[i][0], invalidInputs[i][1]);
                check(testName, false);
            } catch (IllegalArgumentException exception) {
                check(testName, true);
            }
        }
    }

    private static void check(String testName, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + testName);
    }
}