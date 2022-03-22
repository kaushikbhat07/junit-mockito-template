package patientintake;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClinicCalendarTest {
	private ClinicCalendar clinicCalendar;

	@BeforeAll
	static void initAll() {
	}

	@BeforeEach
	void setUp() {
		clinicCalendar = new ClinicCalendar();
	}

	@AfterEach
	void tearDown() {
	}

	@AfterAll
	static void tearDownAll() {
	}

	@Test
	void allowEntryOfAnAppointment() {
		clinicCalendar.addAppointment("Kaushik", "Bhat", "avery",
				"09/01/2018 02:00 pm");

		List<PatientAppointment> appointmentList = clinicCalendar.getAppointments();
		assertNotNull(appointmentList);
		PatientAppointment patientAppointment = appointmentList.get(0);
		assertAll(
				() -> assertEquals(1, appointmentList.size()),
				() -> assertEquals("Kaushik", patientAppointment.getPatientFirstName()),
				() -> assertEquals("Bhat", patientAppointment.getPatientLastName()),
				() -> assertEquals(Doctor.avery, patientAppointment.getDoctor()),
				() -> assertEquals("09/01/2018 02:00 PM", patientAppointment.getAppointmentDateTime()
								.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
						, "Your custom error message here")
		);
	}

	@DisplayName("Throws Runtime Exception when provided date format is incorrect")
	@Test
	void throwsExceptionIfIncorrectDateProvided() {
		assertThrows(RuntimeException.class, () -> clinicCalendar.addAppointment("Kaushik",
				"Bhat", "avery", "09/01/2018 02:00"));
	}
}