package patientintake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClinicCalendarTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void allowEntryOfAnAppointment() {
		ClinicCalendar clinicCalendar = new ClinicCalendar();
		clinicCalendar.addAppointment("Kaushik", "Bhat", "avery", "09/01/2018 02:00 pm");

		List<PatientAppointment> appointmentList = clinicCalendar.getAppointments();
		assertNotNull(appointmentList);
		assertEquals(1, appointmentList.size());
		PatientAppointment patientAppointment = appointmentList.get(0);
		assertEquals("Kaushik", patientAppointment.getPatientFirstName());
		assertEquals("Bhat", patientAppointment.getPatientLastName());
		assertEquals(Doctor.avery, patientAppointment.getDoctor());
		assertEquals("09/01/2018 02:00 PM", patientAppointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")));
	}
}