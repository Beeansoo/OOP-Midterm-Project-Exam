import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        Tutor english = new Tutor("T001", "Ana", Subject.ENGLISH);
        Tutor math = new Tutor("T002", "Ben", Subject.MATH);
        Tutor coding = new Tutor("T003", "Cara", Subject.CODING);

        BookingSystem system = new BookingSystem(
                english, math, coding);

        boolean running = true;

        while (running) {
            System.out.println("\n=== Tutoring Session Booking System ===");
            System.out.println("1. Register student");
            System.out.println("2. View people directory");
            System.out.println("3. Create tutoring session");
            System.out.println("4. View tutoring sessions");
            System.out.println("5. Request a booking");
            System.out.println("6. View bookings");
            System.out.println("7. Approve next pending booking");
            System.out.println("8. Reject next pending booking");
            System.out.println("9. Cancel a booking");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            if (!input.hasNextLine()) {
                break;
            }

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    registerStudent(input, system);
                    break;

                case "2":
                    displayDirectory(system);
                    break;
                case "3":
                    createSession(input, system, english, math, coding);
                    break;

                case "4":
                     displaySessions(system);
                    break;
                
                    case "5":
                    requestBooking(input, system);
                    break;

                case "6":
                     displayBookings(system);
                    break;
                case "7":
                    approveBooking(input, system);
                    break;

                case "8":
                    rejectBooking(input, system);
                    break;

                case "9":
                    cancelBooking(input, system);
                    break;
                case "0":
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid option. Enter a number from 0 to 9.");
            }
        }

        System.out.println(
                "Program closed. Records from this run are not saved.");
        input.close();
    }

    private static void registerStudent(
            Scanner input, BookingSystem system) {
        System.out.print("Student ID: ");

        if (!input.hasNextLine()) {
            return;
        }

        String id = input.nextLine();

        System.out.print("Student name: ");

        if (!input.hasNextLine()) {
            return;
        }

        String name = input.nextLine();

        try {
            Student student = system.registerStudent(id, name);

            System.out.println(
                    "Registered: " + student.getDirectoryDetails());
        } catch (IllegalArgumentException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    private static void displayDirectory(BookingSystem system) {
        System.out.println("\n=== People Directory ===");

        for (Person person : system.getPeople()) {
            System.out.println(person.getDirectoryDetails());
        }
    }

    private static void createSession(
        Scanner input, BookingSystem system,
        Tutor english, Tutor math, Tutor coding) {

    DateTimeFormatter dateFormat = DateTimeFormatter
            .ofPattern("uuuu/MM/dd")
            .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    try {
        System.out.println("\n=== Create Tutoring Session ===");
        System.out.println("1. English - Monday and Thursday");
        System.out.println("2. Math - Tuesday and Friday");
        System.out.println("3. Coding - Wednesday and Saturday");

        String subjectChoice = readLine(input, "Choose subject: ");
        Tutor tutor;
        String suggestedId;

        switch (subjectChoice) {
            case "1":
                tutor = english;
                suggestedId = "E001";
                break;
            case "2":
                tutor = math;
                suggestedId = "M001";
                break;
            case "3":
                tutor = coding;
                suggestedId = "C001";
                break;
            default:
                throw new IllegalArgumentException(
                        "Choose subject 1, 2, or 3.");
        }

        System.out.println("Selected subject: " + tutor.getSubject());
        System.out.println("Assigned tutor: " + tutor.getName());

        String id = readLine(input,
                "Session ID for " + tutor.getSubject()
                        + " (example: " + suggestedId + "): ");

        LocalDate date;

        while (true) {
            String dateText = readLine(input, "Date (YYYY/MM/DD): ");

            try {
                date = LocalDate.parse(dateText, dateFormat);

                if (date.isBefore(LocalDate.now())) {
                    System.out.println(
                            "That date is in the past. Today is "
                                    + LocalDate.now().format(dateFormat));
                    continue;
                }

                if (!tutor.getSubject().isAllowedOn(date.getDayOfWeek())) {
                    System.out.println(
                            "This subject is not available on "
                                    + date.getDayOfWeek()
                                    + ". Choose one of its assigned days.");
                    continue;
                }

                break;
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Enter a valid date such as 2026/10/12.");
            }
        }

        SessionSlot slot;

        while (true) {
            System.out.println("1. 9:00 AM - 10:30 AM");
            System.out.println("2. 11:00 AM - 12:30 PM");
            System.out.println("3. 2:00 PM - 3:30 PM");

            String slotChoice = readLine(input, "Choose slot: ");

            switch (slotChoice) {
                case "1":
                    slot = SessionSlot.MORNING_FIRST;
                    break;
                case "2":
                    slot = SessionSlot.MORNING_SECOND;
                    break;
                case "3":
                    slot = SessionSlot.AFTERNOON;
                    break;
                default:
                    System.out.println("Choose slot 1, 2, or 3.");
                    continue;
            }

            if (!date.atTime(slot.getStartTime())
                    .isAfter(LocalDateTime.now())) {
                System.out.println(
                        "That slot has already started. "
                                + "Restart session creation to choose another date.");
                return;
            }

            break;
        }

        BigDecimal fee = new BigDecimal(
                readLine(input, "Session fee in PHP: "));

        TutoringSession session = new TutoringSession(
                id, tutor, date, slot, fee);

        system.addSession(session, LocalDateTime.now());

        System.out.println(
                "Session added successfully for this program run.");

        displaySessions(system);

    } catch (NumberFormatException exception) {
        System.out.println(
                "Error: Enter a numeric fee, such as 400.00.");
    } catch (IllegalArgumentException | IllegalStateException exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}

private static void displaySessions(BookingSystem system) {
    System.out.println("\n=== Tutoring Sessions ===");

    if (system.getSessions().isEmpty()) {
        System.out.println("No sessions have been created.");
        return;
    }

    DateTimeFormatter dateFormat =
        DateTimeFormatter.ofPattern("uuuu/MM/dd");

    DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern("HH:mm");

    LocalDateTime now = LocalDateTime.now();

    for (TutoringSession session : system.getSessions()) {
        String availability;

        if (!now.isBefore(session.getStartTime())) {
            availability = "Closed";
        } else if (system.isSessionAvailable(session.getSessionId())) {
            availability = "Available";
        } else {
            availability = "Full";
        }

        System.out.println(
                session.getSessionId()
                        + " | " + session.getTutor().getSubject()
                        + " | Tutor: " + session.getTutor().getName()
                        + " | Tutor ID: " + session.getTutor().getId()
                        + " | " + session.getStartTime().format(dateFormat)
                        + " | " + session.getStartTime().format(timeFormat)
                        + "-" + session.getEndTime().format(timeFormat)
                        + " | PHP " + session.getSessionFee()
                        + " | " + availability);
    }
}

private static String readLine(Scanner input, String prompt) {
    System.out.print(prompt);

    if (!input.hasNextLine()) {
        throw new IllegalStateException("Input ended.");
    }

    return input.nextLine().trim();
}    

    private static void requestBooking(
        Scanner input, BookingSystem system) {
    if (system.getSessions().isEmpty()) {
        System.out.println("Create a tutoring session first.");
        return;
    }

    displaySessions(system);

    try {
        String studentId = readLine(input, "Registered student ID: ");
        String sessionId = readLine(input, "Session ID to request: ");

        Booking booking = system.requestBooking(
                studentId, sessionId, LocalDateTime.now());

        System.out.println(
                "Request number: " + booking.getRequestNumber());
        System.out.println(
                "Student: " + booking.getStudent().getName());
        System.out.println(
                "Subject: " + booking.getSession().getTutor().getSubject());
        System.out.println(
                "Booking fee: PHP " + booking.getBookingFee());
        System.out.println(
                "Status: " + booking.getStatus());
        System.out.println(
                "Your request is waiting for tutor approval.");

    } catch (IllegalArgumentException | IllegalStateException exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}

private static void displayBookings(BookingSystem system) {
    System.out.println("\n=== Bookings ===");

    if (system.getBookings().isEmpty()) {
        System.out.println("No bookings have been requested.");
        return;
    }

    DateTimeFormatter scheduleFormat =
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm");

    for (Booking booking : system.getBookings()) {
        System.out.println(
                "Request " + booking.getRequestNumber()
                        + " | Student: " + booking.getStudent().getName()
                        + " (" + booking.getStudent().getId() + ")"
                        + " | Session: "
                        + booking.getSession().getSessionId()
                        + " | "
                        + booking.getSession().getStartTime()
                                .format(scheduleFormat)
                        + " | Status: " + booking.getStatus()
                        + " | Booking fee: PHP " + booking.getBookingFee()
                        + " | Cancellation fee: PHP "
                        + booking.getCancellationFee());

        if (!booking.getRejectionReason().isEmpty()) {
            System.out.println(
                    "  Rejection reason: " + booking.getRejectionReason());
        }
    }
}

    private static void approveBooking(
        Scanner input, BookingSystem system) {
    displaySessions(system);

    try {
        String tutorId = readLine(
        input, "Assigned tutor ID (example: T001, without 'Tutor'): ");
        String sessionId = readLine(input, "Session ID: ");

        String result = system.approveNextBooking(
                tutorId, sessionId, LocalDateTime.now());

        System.out.println(result);

    } catch (IllegalArgumentException | IllegalStateException exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}

private static void rejectBooking(
        Scanner input, BookingSystem system) {
    displaySessions(system);

    try {
        String tutorId = readLine(input, "Assigned tutor ID: ");
        String sessionId = readLine(input, "Session ID: ");

        Booking next = system.getNextPendingBooking(sessionId);

        if (next == null) {
            System.out.println("No pending requests for this session.");
            return;
        }

        System.out.println(
                "Next request: " + next.getRequestNumber()
                        + " | Student: " + next.getStudent().getName());

        String reason = readLine(input, "Rejection reason: ");

        system.rejectNextBooking(tutorId, sessionId, reason);

        System.out.println(
                "Request " + next.getRequestNumber()
                        + " rejected. Reason: "
                        + next.getRejectionReason());

    } catch (IllegalArgumentException | IllegalStateException exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}

private static void cancelBooking(
        Scanner input, BookingSystem system) {
    displayBookings(system);

    try {
        int requestNumber = Integer.parseInt(
                readLine(input, "Request number to cancel: "));

        Booking selected = null;

        for (Booking booking : system.getBookings()) {
            if (booking.getRequestNumber() == requestNumber) {
                selected = booking;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Error: Booking not found.");
            return;
        }

        System.out.println(
                "Student: " + selected.getStudent().getName()
                        + " | Session: "
                        + selected.getSession().getSessionId()
                        + " | Status: " + selected.getStatus());

        System.out.println(
                "Approved bookings incur a 10% cancellation fee."
                        + " Pending bookings have no cancellation fee.");

        String confirmation = readLine(
                input, "Confirm cancellation? (Y/N): ");

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Cancellation not performed.");
            return;
        }

        system.cancelBooking(requestNumber, LocalDateTime.now());

        System.out.println(
                "Booking cancelled. Cancellation fee: PHP "
                        + selected.getCancellationFee());

    } catch (NumberFormatException exception) {
        System.out.println("Error: Enter a whole-number request number.");
    } catch (IllegalArgumentException | IllegalStateException exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}
}
