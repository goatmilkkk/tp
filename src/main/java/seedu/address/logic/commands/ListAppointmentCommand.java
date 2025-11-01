package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ViewMode;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.patient.Patient;

/**
 * List appointments of the specified patient in the address book.
 */
public class ListAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "list-appt";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": List appointments of a patient identified by the index number used in the displayed patient list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Listed all appointments for %1$s [%2$s]";

    private final Index targetIndex;

    /**
     * Creates a ListAppointmentCommand to list appointments of the patient at the specified index.
     */
    public ListAppointmentCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Patient> lastShownList = model.getFilteredPatientList();

        if (model.getViewMode() != ViewMode.PATIENT_LIST) {
            throw new CommandException(Messages.MESSAGE_NOT_VIEWING_PATIENT_LIST);
        }

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
        }
        Patient targetPatient = lastShownList.get(targetIndex.getZeroBased());

        // Fetch the time now
        LocalDateTime now = LocalDateTime.now();

        // Sorts appointment list to display upcoming first and past appointments after
        List<Appointment> sortedAppointments = targetPatient.getAppointments().stream()
                .sorted(Comparator.comparing((Appointment a) -> a.getDateTime().isBefore(now))
                        .thenComparing(Appointment::getDateTime))
                .toList();

        // Update Patient to have sorted list
        Patient updatedPatient = new Patient(
                targetPatient.getName(),
                targetPatient.getNric(),
                targetPatient.getGender(),
                targetPatient.getPhone(),
                targetPatient.getEmail(),
                targetPatient.getDob(),
                targetPatient.getAddress(),
                targetPatient.getTags(),
                new ArrayList<>(sortedAppointments)
        );

        // Update the model so subsequent commands see the same sorted order
        model.setPatient(targetPatient, updatedPatient);
        model.setSelectedPatient(updatedPatient);
        model.setViewMode(ViewMode.PATIENT_APPOINTMENT_LIST);

        return new CommandResult(String.format(MESSAGE_SUCCESS, targetPatient.getName(),
                targetPatient.getNric()), false, true, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListAppointmentCommand)) {
            return false;
        }

        ListAppointmentCommand otherListAppointmentCommand = (ListAppointmentCommand) other;
        return targetIndex.equals(otherListAppointmentCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
