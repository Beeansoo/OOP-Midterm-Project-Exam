import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingSystem {
    private final List<Student> students = new ArrayList<>();
    private final List<Tutor> tutors = new ArrayList<>();
    private static final int MAX_SESSIONS_PER_DAY = 3;
    private final List<Booking> bookings = new ArrayList<>();
    private int nextRequestNumber = 1;

private final List<TutoringSession> sessions = new ArrayList<>();
    public void addSession(TutoringSession session, LocalDateTime now) {
    if (session == null || now == null) {
        throw new IllegalArgumentException(
                "Session and current time are required.");
    }

    if (!session.getStartTime().isAfter(now)) {
        throw new IllegalArgumentException(
                "Session must start in the future.");
    }

    if (!tutors.contains(session.getTutor())) {
        throw new IllegalArgumentException(
                "Session must use a registered tutor object.");
    }

    for (TutoringSession existing : sessions) {
        if (existing.getSessionId().equals(session.getSessionId())) {
            throw new IllegalArgumentException(
                    "Session ID already exists.");
        }
    }

    LocalDate date = session.getStartTime().toLocalDate();

    if (!session.getTutor().getSubject()
            .isAllowedOn(date.getDayOfWeek())) {
        throw new IllegalArgumentException(
                "This subject is not scheduled on the selected day.");
    }

    if (countTutorSessionsOnDate(
            session.getTutor().getId(), date) >= MAX_SESSIONS_PER_DAY) {
        throw new IllegalArgumentException(
                "Tutor already has three sessions on this date.");
    }

    if (hasTutorConflict(session)) {
        throw new IllegalArgumentException(
                "Tutor already has an overlapping session.");
    }

    sessions.add(session);
}

private int countTutorSessionsOnDate(String tutorId, LocalDate date) {
    int count = 0;

    for (TutoringSession session : sessions) {
        if (session.getTutor().getId().equals(tutorId)
                && session.getStartTime().toLocalDate().equals(date)) {
            count++;
        }
    }

    return count;
}

private boolean hasTutorConflict(TutoringSession proposed) {
    for (TutoringSession existing : sessions) {
        if (existing.getTutor().getId()
                .equals(proposed.getTutor().getId())
                && existing.overlaps(proposed)) {
            return true;
        }
    }

    return false;
}

public List<TutoringSession> getSessions() {
    return new ArrayList<>(sessions);
}

    public BookingSystem(Tutor englishTutor, Tutor mathTutor,
                         Tutor codingTutor) {
        if (englishTutor == null || mathTutor == null
                || codingTutor == null) {
            throw new IllegalArgumentException(
                    "Three tutors are required.");
        }

        if (englishTutor.getSubject() != Subject.ENGLISH
                || mathTutor.getSubject() != Subject.MATH
                || codingTutor.getSubject() != Subject.CODING) {
            throw new IllegalArgumentException(
                    "Provide tutors in English, Math, Coding order.");
        }

        if (englishTutor.getId().equals(mathTutor.getId())
                || englishTutor.getId().equals(codingTutor.getId())
                || mathTutor.getId().equals(codingTutor.getId())) {
            throw new IllegalArgumentException(
                    "Tutor IDs must be unique.");
        }

        tutors.add(englishTutor);
        tutors.add(mathTutor);
        tutors.add(codingTutor);
    }

    public Student registerStudent(String id, String name) {
        Student student = new Student(id, name);

        for (Student existing : students) {
            if (existing.getId().equals(student.getId())) {
                throw new IllegalArgumentException(
                        "Student ID already exists.");
            }
        }

        students.add(student);
        return student;
    }

    public List<Person> getPeople() {
        List<Person> people = new ArrayList<>();

        people.addAll(students);
        people.addAll(tutors);

        return people;
    }

    public Booking requestBooking(String studentId, String sessionId,
                              LocalDateTime now) {
    if (now == null) {
        throw new IllegalArgumentException(
                "Current date and time are required.");
    }

    Student student = findStudent(studentId);
    TutoringSession session = findSession(sessionId);

    if (!now.isBefore(session.getStartTime())) {
        throw new IllegalStateException(
                "Cannot request a session that has already started.");
    }

    for (Booking existing : bookings) {
        boolean sameStudent =
                existing.getStudent().getId().equals(student.getId());

        boolean sameSession =
                existing.getSession().getSessionId()
                        .equals(session.getSessionId());

        boolean active =
                existing.getStatus() == BookingStatus.PENDING
                        || existing.getStatus() == BookingStatus.APPROVED;

        if (sameStudent && sameSession && active) {
            throw new IllegalArgumentException(
                    "Student already has an active request for this session.");
        }
    }

    Booking booking = new Booking(
            nextRequestNumber, student, session);

    bookings.add(booking);
    nextRequestNumber++;

    return booking;
}

private Student findStudent(String studentId) {
    if (studentId == null || studentId.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Student ID is required.");
    }

    for (Student student : students) {
        if (student.getId().equals(studentId.trim())) {
            return student;
        }
    }

    throw new IllegalArgumentException("Student not found.");
}

private TutoringSession findSession(String sessionId) {
    if (sessionId == null || sessionId.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Session ID is required.");
    }

    for (TutoringSession session : sessions) {
        if (session.getSessionId().equals(sessionId.trim())) {
            return session;
        }
    }

    throw new IllegalArgumentException("Session not found.");
}

private Booking findOldestPending(String sessionId) {
    Booking oldest = null;

    for (Booking booking : bookings) {
        boolean matchesSession =
                booking.getSession().getSessionId().equals(sessionId);

        if (matchesSession
                && booking.getStatus() == BookingStatus.PENDING) {
            if (oldest == null
                    || booking.getRequestNumber()
                            < oldest.getRequestNumber()) {
                oldest = booking;
            }
        }
    }

    return oldest;
}

public Booking getNextPendingBooking(String sessionId) {
    TutoringSession session = findSession(sessionId);
    return findOldestPending(session.getSessionId());
}

public List<Booking> getBookings() {
    return new ArrayList<>(bookings);
}

    public boolean isSessionAvailable(String sessionId) {
    TutoringSession session = findSession(sessionId);

    for (Booking booking : bookings) {
        if (booking.getSession() == session
                && booking.getStatus() == BookingStatus.APPROVED) {
            return false;
        }
    }

    return true;
}

public String approveNextBooking(String tutorId, String sessionId,
                                 LocalDateTime now) {
    TutoringSession session = findSession(sessionId);
    requireAssignedTutor(tutorId, session);
    requireBeforeSession(session, now);

    Booking next = findOldestPending(session.getSessionId());

    if (next == null) {
        return "No pending requests for this session.";
    }

    if (!isSessionAvailable(session.getSessionId())) {
        return "Session is full. Requests remain pending.";
    }

    if (hasTutorConflictForApproval(session)) {
        throw new IllegalStateException(
                "Tutor schedule conflict. Approval blocked.");
    }

    if (hasStudentConflict(next)) {
        next.reject("Student has an overlapping approved booking.");

        return "Request " + next.getRequestNumber()
                + " rejected: student schedule conflict.";
    }

    next.approve(now);

    return "Request " + next.getRequestNumber() + " approved.";
}

public void rejectNextBooking(String tutorId, String sessionId,
                              String reason) {
    TutoringSession session = findSession(sessionId);
    requireAssignedTutor(tutorId, session);

    Booking next = findOldestPending(session.getSessionId());

    if (next == null) {
        throw new IllegalStateException(
                "No pending requests for this session.");
    }

    next.reject(reason);
}

public void cancelBooking(int requestNumber, LocalDateTime now) {
    Booking booking = findBooking(requestNumber);

    Cancellable cancellable = booking;
    cancellable.cancel(now);
}

private Booking findBooking(int requestNumber) {
    for (Booking booking : bookings) {
        if (booking.getRequestNumber() == requestNumber) {
            return booking;
        }
    }

    throw new IllegalArgumentException("Booking not found.");
}

private void requireAssignedTutor(String tutorId,
                                  TutoringSession session) {
    if (tutorId == null
            || !session.getTutor().getId().equals(tutorId.trim())) {
        throw new IllegalArgumentException(
                "Only the assigned tutor can review this session.");
    }
}

private void requireBeforeSession(TutoringSession session,
                                  LocalDateTime now) {
    if (now == null) {
        throw new IllegalArgumentException(
                "Current date and time are required.");
    }

    if (!now.isBefore(session.getStartTime())) {
        throw new IllegalStateException(
                "Cannot approve bookings once the session starts.");
    }
}

private boolean hasStudentConflict(Booking proposed) {
    for (Booking existing : bookings) {
        boolean sameStudent = existing.getStudent().getId()
                .equals(proposed.getStudent().getId());

        if (sameStudent
                && existing.getStatus() == BookingStatus.APPROVED
                && existing.getSession().overlaps(proposed.getSession())) {
            return true;
        }
    }

    return false;
}

private boolean hasTutorConflictForApproval(TutoringSession proposed) {
    for (TutoringSession existing : sessions) {
        if (existing != proposed
                && existing.getTutor().getId()
                        .equals(proposed.getTutor().getId())
                && existing.overlaps(proposed)) {
            return true;
        }
    }

    return false;
}
}

