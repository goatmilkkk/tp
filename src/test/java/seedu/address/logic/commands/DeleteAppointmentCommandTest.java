package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.DeleteAppointmentCommand.MESSAGE_APPOINTMENT_NOT_FOUND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPOINTMENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPOINTMENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static seedu.address.testutil.TypicalPatients.getTypicalDoctorBase;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.ViewMode;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.patient.Patient;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAppointmentCommand}.
 */
public class DeleteAppointmentCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalDoctorBase(), new UserPrefs());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    @Test
    public void execute_validIndex_success() {
        Patient patient = model.getFilteredPatientList().get(INDEX_SECOND_PATIENT.getZeroBased());

        model.setSelectedPatient(patient);
        model.setViewMode(ViewMode.PATIENT_APPOINTMENT_LIST);

        DeleteAppointmentCommand deleteAppointmentCommand = new DeleteAppointmentCommand(INDEX_FIRST_APPOINTMENT);

        Patient editedPatient = new Patient(patient.getName(), patient.getNric(), patient.getGender(),
                patient.getPhone(), patient.getEmail(), patient.getDob(), patient.getAddress(), patient.getTags(),
                new ArrayList<>(patient.getAppointments()));
        Appointment deletedAppointment = editedPatient.getAppointments().remove(INDEX_FIRST_APPOINTMENT.getZeroBased());

        String expectedMessage = String.format(DeleteAppointmentCommand.MESSAGE_DELETE_APPOINTMENT_SUCCESS,
                Messages.format(patient), deletedAppointment);

        ModelManager expectedModel = new ModelManager(model.getDoctorBase(), new UserPrefs());
        expectedModel.setPatient(patient, editedPatient);

        assertCommandSuccess(deleteAppointmentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_selectedPatientNull_throwsCommandException() {
        // Set selectedPatient to null to indicate not viewing Appointments
        model.setSelectedPatient(null);

        DeleteAppointmentCommand deleteAppointmentCommand = new DeleteAppointmentCommand(INDEX_FIRST_APPOINTMENT);

        assertCommandFailure(deleteAppointmentCommand, model, Messages.MESSAGE_NOT_VIEWING_APPOINTMENT);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Patient patient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Index outOfBoundsIndex = Index.fromOneBased(patient.getAppointments().size() + 1);
        DeleteAppointmentCommand deleteAppointmentCommand = new DeleteAppointmentCommand(outOfBoundsIndex);
        model.setSelectedPatient(patient);
        model.setViewMode(ViewMode.PATIENT_APPOINTMENT_LIST);

        assertCommandFailure(deleteAppointmentCommand, model,
                String.format(MESSAGE_APPOINTMENT_NOT_FOUND, outOfBoundsIndex.getOneBased()));
    }

    @Test
    public void equals() {
        DeleteAppointmentCommand deleteAppointmentFirstCommand =
                new DeleteAppointmentCommand(INDEX_FIRST_APPOINTMENT);
        DeleteAppointmentCommand deleteAppointmentSecondCommand =
                new DeleteAppointmentCommand(INDEX_SECOND_APPOINTMENT);

        // same object -> returns true
        assertTrue(deleteAppointmentFirstCommand.equals(deleteAppointmentFirstCommand));

        // same values -> returns true
        DeleteAppointmentCommand deleteAppointmentFirstCommandCopy =
                new DeleteAppointmentCommand(INDEX_FIRST_APPOINTMENT);
        assertTrue(deleteAppointmentFirstCommand.equals(deleteAppointmentFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteAppointmentFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteAppointmentFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(deleteAppointmentFirstCommand.equals(deleteAppointmentSecondCommand));

    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteAppointmentCommand deleteAppointmentCommand = new DeleteAppointmentCommand(targetIndex);
        String expected = DeleteAppointmentCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteAppointmentCommand.toString());
    }
}
