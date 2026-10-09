public class Tutor extends Person {
    private final Subject subject;

    public Tutor(String id, String name, Subject subject) {
        super(id, name);

        if (subject == null) {
            throw new IllegalArgumentException(
                    "Tutor subject must not be null.");
        }

        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String getDirectoryDetails() {
        return "Tutor " + getId() + " | " + getName()
                + " | Subject: " + subject;
    }
}