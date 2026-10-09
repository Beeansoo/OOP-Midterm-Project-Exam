public abstract class Person {
    private final String id;
    private String name;

    protected Person(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID must not be blank.");
        }

        this.id = id.trim();
        setName(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name must not be blank.");
        }

        this.name = name.trim();
    }

    public abstract String getDirectoryDetails();
}