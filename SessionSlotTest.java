import java.time.LocalTime;

public class SessionSlotTest {
    public static void main(String[] args) {
        LocalTime[] expectedStarts = {
            LocalTime.of(9, 0),
            LocalTime.of(11, 0),
            LocalTime.of(14, 0)
        };

        int index = 0;

        for (SessionSlot slot : SessionSlot.values()) {
            LocalTime actualStart = slot.getStartTime();

            if (actualStart.equals(expectedStarts[index])) {
                System.out.println("PASS: " + slot + " starts at " + actualStart);
            } else {
                System.out.println("FAIL: " + slot
                        + " expected " + expectedStarts[index]
                        + " but got " + actualStart);
            }

            index++;
        }
    }
}