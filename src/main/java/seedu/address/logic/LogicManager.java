package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.DoctorBaseParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyDoctorBase;
import seedu.address.model.ViewMode;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final DoctorBaseParser doctorBaseParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        doctorBaseParser = new DoctorBaseParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        // Ensure delete-appt & edit-appt return view-mode error when executed outside of appointments list view
        String trimmedCommand = commandText.trim();
        if (!trimmedCommand.isEmpty()) {
            String commandWord = trimmedCommand.split("\\s+", 2)[0];
            if ((EditAppointmentCommand.COMMAND_WORD.equals(commandWord)
                    || DeleteAppointmentCommand.COMMAND_WORD.equals(commandWord))
                    && model.getViewMode() != ViewMode.PATIENT_APPOINTMENT_LIST) {
                throw new CommandException(Messages.MESSAGE_NOT_VIEWING_APPOINTMENT);
            }
        }

        CommandResult commandResult;
        Command command = doctorBaseParser.parseCommand(commandText);
        commandResult = command.execute(model);

        try {
            storage.saveDoctorBase(model.getDoctorBase());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyDoctorBase getDoctorBase() {
        return model.getDoctorBase();
    }

    @Override
    public ObservableList<Patient> getFilteredPatientList() {
        return model.getFilteredPatientList();
    }

    @Override
    public Patient getSelectedPatient() {
        return model.getSelectedPatient();
    }

    @Override
    public ObservableList<Appointment> getUpcomingAppointments() {
        return model.getUpcomingAppointments();
    }

    @Override
    public Path getDoctorBaseFilePath() {
        return model.getDoctorBaseFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
