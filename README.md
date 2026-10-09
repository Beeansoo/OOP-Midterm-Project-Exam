Tutoring Session Booking System
Vince Lachica
Java console application for in-person one-on-one tutoring.

Requirements
JDK 17 recommended. No external Java libraries.
Open a terminal in this package directory.

Compile application
javac -d build src/*.java

Run
java -cp build Main

Compile application and tests
javac -d build src/*.java tests/*.java

Run workflow tests
java -cp build BookingWorkflowTest
java -cp build SchedulingTest
java -cp build BookingRequestTest

PowerShell run all test programs
Get-ChildItem tests/*Test.java | ForEach-Object { java -cp build $_.BaseName }

Inspect output for FAIL; these simple tests do not set a failing exit code.
SubjectTest prints the allowed days for manual comparison.

Menu
1 Register student; 2 Directory; 3 Create session; 4 View sessions;
5 Request booking; 6 View bookings; 7 Approve next pending;
8 Reject next pending; 9 Cancel booking; 0 Exit.

Tutor IDs: T001 English (Ana), T002 Math (Ben), T003 Coding (Cara).
English Monday/Thursday; Math Tuesday/Friday; Coding Wednesday/Saturday.
Date input: YYYY/MM/DD. Slots: 09:00, 11:00, 14:00; duration 90 minutes.
Choose future dates according to the computer clock.
Create students and sessions before requesting bookings.
Only approved bookings occupy a session; pending requests follow FIFO per session.
Approved cancellation before start costs 10 percent; pending cancellation is free.
No login, payment processing, or file persistence. Closing the app clears records.

Package status
Source files are unchanged from ME Project(1).zip.
Tests were retained from the earlier ME project.zip, excluding obsolete IndividualSessionTest.
Report is a draft: insert authentic screenshots, cover details, and personal reflection.
Add the complete conversation export and your recorded video separately.
