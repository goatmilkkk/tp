package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103t-w10-3.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE =
              "Pro Tip: Enter a command without parameters to see usage examples!\n\n"
            + "[Patient Commands]\n"
            + "1. Add Patient: add n/NAME i/NRIC g/GENDER p/PHONE e/EMAIL d/DATEOFBIRTH a/ADDRESS [mh/MEDICALHISTORY]\n"
            + "2. List Patients: list\n"
            + "3. Find Patient: find KEYWORD [MORE_KEYWORDS]\n"
            + "4. Edit Patient: edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]...\n"
            + "5. Delete Patient: delete INDEX\n"
            + "6. Clear All Patients: clear\n"
            + "\n"
            + "[Appointment Commands]\n"
            + "1. Add Appointment: add-appt INDEX at/APPOINTMENT_TITLE ad/APPOINTMENT_DATE\n"
            + "2. List All Upcoming Appointments: list-appt-upcoming\n"
            + "3. List Selected Patient Appointments: list-appt INDEX\n"
            + "4. Edit Appointment: edit-appt INDEX [at/APPOINTMENT_TITLE] [ad/APPOINTMENT_DATE]\n"
            + "5. Delete Appointment: delete-appt INDEX\n"
            + "\n"
            + "User guide: " + USERGUIDE_URL + "\n";


    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
