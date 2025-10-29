package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_ECZEMA;
import static seedu.address.model.appointment.Appointment.DATETIME_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BOB;
import static seedu.address.testutil.TypicalPatients.getTypicalDoctorBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.Title;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.exceptions.DuplicatePatientException;
import seedu.address.testutil.AppointmentBuilder;
import seedu.address.testutil.PatientBuilder;

public class DoctorBaseTest {

    private final DoctorBase doctorBase = new DoctorBase();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), doctorBase.getPatientList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> doctorBase.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyDoctorBase_replacesData() {
        DoctorBase newData = getTypicalDoctorBase();
        doctorBase.resetData(newData);
        assertEquals(newData, doctorBase);
    }

    @Test
    public void resetData_withDuplicatePatients_throwsDuplicatePatientException() {
        // Two patients with the same identity fields
        Patient editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_ECZEMA)
                .build();
        List<Patient> newPatients = Arrays.asList(ALICE, editedAlice);
        DoctorBaseStub newData = new DoctorBaseStub(newPatients);

        assertThrows(DuplicatePatientException.class, () -> doctorBase.resetData(newData));
    }

    @Test
    public void hasPatient_nullPatient_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> doctorBase.hasPatient(null));
    }

    @Test
    public void hasPatient_patientNotInDoctorBase_returnsFalse() {
        assertFalse(doctorBase.hasPatient(ALICE));
    }

    @Test
    public void hasPatient_patientInDoctorBase_returnsTrue() {
        doctorBase.addPatient(ALICE);
        assertTrue(doctorBase.hasPatient(ALICE));
    }

    @Test
    public void hasPatient_patientWithSameIdentityFieldsInDoctorBase_returnsTrue() {
        doctorBase.addPatient(ALICE);
        Patient editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_ECZEMA)
                .build();
        assertTrue(doctorBase.hasPatient(editedAlice));
    }

    @Test
    public void getPatientList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> doctorBase.getPatientList().remove(0));
    }

    @Test
    public void getUpcomingAppointmentList_noPatients_emptyList() {
        ObservableList<Appointment> upcoming = doctorBase.getUpcomingAppointmentList();
        assertTrue(upcoming.isEmpty());
    }

    @Test
    public void getUpcomingAppointmentList_withPastAndFutureAppointments_returnsOnlyFuture() {
        LocalDateTime now = LocalDateTime.now();

        Appointment past = new AppointmentBuilder().withDateTime(now.minusDays(2).format(DATETIME_FORMAT)).build();
        Appointment future = new AppointmentBuilder().withDateTime(now.plusDays(3).format(DATETIME_FORMAT)).build();
        ArrayList<Appointment> appointments = new ArrayList<>();
        appointments.add(past);
        appointments.add(future);

        Patient patient = new PatientBuilder().withAppointments(appointments).build();
        doctorBase.addPatient(patient);

        ObservableList<Appointment> upcoming = doctorBase.getUpcomingAppointmentList();

        // Update appointments to include names and nric to follow getUpcomingAppointmentsList changes
        past = new Appointment(
                new Title(past.getTitle() + " (" + patient.getName() + ", " + patient.getNric() + ")"),
                past.getDateTime());
        future = new Appointment(
                new Title(future.getTitle() + " (" + patient.getName() + ", " + patient.getNric() + ")"),
                future.getDateTime());

        assertEquals(1, upcoming.size());
        assertTrue(upcoming.contains(future));
        assertFalse(upcoming.contains(past));
    }

    @Test
    public void getUpcomingAppointmentList_multiplePatients_mergesAndSorts() {
        LocalDateTime now = LocalDateTime.now();

        Appointment a1 = new AppointmentBuilder().withDateTime(now.plusDays(3).format(DATETIME_FORMAT)).build();
        Appointment a2 = new AppointmentBuilder().withDateTime(now.plusDays(1).format(DATETIME_FORMAT)).build();
        Appointment a3 = new AppointmentBuilder().withDateTime(now.plusDays(2).format(DATETIME_FORMAT)).build();

        ArrayList<Appointment> aList1 = new ArrayList<>();
        aList1.add(a1);
        ArrayList<Appointment> aList2 = new ArrayList<>();
        aList2.add(a2);
        aList2.add(a3);

        Patient p1 = new PatientBuilder(ALICE).withAppointments(aList1).build();
        Patient p2 = new PatientBuilder(BOB).withAppointments(aList2).build();

        doctorBase.addPatient(p1);
        doctorBase.addPatient(p2);

        ObservableList<Appointment> upcoming = doctorBase.getUpcomingAppointmentList();

        // Update appointments to include names and nric to follow getUpcomingAppointmentsList changes
        a1 = new Appointment(
                new Title(a1.getTitle() + " (" + p1.getName() + ", " + p1.getNric() + ")"),
                a1.getDateTime());
        a2 = new Appointment(
                new Title(a2.getTitle() + " (" + p2.getName() + ", " + p2.getNric() + ")"),
                a2.getDateTime());
        a3 = new Appointment(
                new Title(a3.getTitle() + " (" + p2.getName() + ", " + p2.getNric() + ")"),
                a3.getDateTime());

        assertEquals(3, upcoming.size());
        // Check sorting order
        assertEquals(a2, upcoming.get(0));
        assertEquals(a3, upcoming.get(1));
        assertEquals(a1, upcoming.get(2));
    }

    @Test
    public void getUpcomingAppointmentList_allPastAppointments_returnsEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        Appointment past1 = new AppointmentBuilder().withDateTime(now.minusDays(5).format(DATETIME_FORMAT)).build();
        Appointment past2 = new AppointmentBuilder().withDateTime(now.minusDays(1).format(DATETIME_FORMAT)).build();

        ArrayList<Appointment> appointments = new ArrayList<>();
        appointments.add(past1);
        appointments.add(past2);

        Patient p = new PatientBuilder().withAppointments(appointments).build();
        doctorBase.addPatient(p);

        ObservableList<Appointment> upcoming = doctorBase.getUpcomingAppointmentList();
        assertTrue(upcoming.isEmpty());
    }

    @Test
    public void getUpcomingAppointmentList_listIsUnmodifiable_throwsExceptionOnModify() {
        Patient p = new PatientBuilder().build();
        doctorBase.addPatient(p);

        ObservableList<Appointment> upcoming = doctorBase.getUpcomingAppointmentList();
        assertThrows(UnsupportedOperationException.class, () ->
                upcoming.add(new AppointmentBuilder().build()));
    }

    @Test
    public void toStringMethod() {
        String expected = DoctorBase.class.getCanonicalName() + "{patients=" + doctorBase.getPatientList() + "}";
        assertEquals(expected, doctorBase.toString());
    }

    /**
     * A stub ReadOnlyDoctorBase whose patients list can violate interface constraints.
     */
    private static class DoctorBaseStub implements ReadOnlyDoctorBase {
        private final ObservableList<Patient> patients = FXCollections.observableArrayList();

        DoctorBaseStub(Collection<Patient> patients) {
            this.patients.setAll(patients);
        }

        @Override
        public ObservableList<Patient> getPatientList() {
            return patients;
        }

        @Override
        public ObservableList<Appointment> getUpcomingAppointmentList() {
            throw new AssertionError("This method should not be called.");
        }
    }

}
