package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.Title;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.UniquePatientList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePatient comparison)
 */
public class DoctorBase implements ReadOnlyDoctorBase {

    private final UniquePatientList patients;
    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        patients = new UniquePatientList();
    }

    public DoctorBase() {}

    /**
     * Creates an DoctorBase using the Patients in the {@code toBeCopied}
     */
    public DoctorBase(ReadOnlyDoctorBase toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the patient list with {@code patients}.
     * {@code patients} must not contain duplicate patients.
     */
    public void setPatients(List<Patient> patients) {
        this.patients.setPatients(patients);
    }

    /**
     * Resets the existing data of this {@code DoctorBase} with {@code newData}.
     */
    public void resetData(ReadOnlyDoctorBase newData) {
        requireNonNull(newData);

        setPatients(newData.getPatientList());
    }

    //// patient-level operations

    /**
     * Returns true if a patient with the same identity as {@code patient} exists in the address book.
     */
    public boolean hasPatient(Patient patient) {
        requireNonNull(patient);
        return patients.contains(patient);
    }

    /**
     * Adds a patient to the address book.
     * The patient must not already exist in the address book.
     */
    public void addPatient(Patient p) {
        patients.add(p);
    }

    /**
     * Replaces the given patient {@code target} in the list with {@code editedPatient}.
     * {@code target} must exist in the address book.
     * The patient identity of {@code editedPatient} must
     * not be the same as another existing patient in the address book.
     */
    public void setPatient(Patient target, Patient editedPatient) {
        requireNonNull(editedPatient);

        patients.setPatient(target, editedPatient);
    }

    /**
     * Removes {@code key} from this {@code DoctorBase}.
     * {@code key} must exist in the address book.
     */
    public void removePatient(Patient key) {
        patients.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("patients", patients)
                .toString();
    }

    @Override
    public ObservableList<Patient> getPatientList() {
        return patients.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Appointment> getUpcomingAppointmentList() {
        List<Appointment> upcomingAppointments = getPatientList().stream()
                .flatMap(patient -> patient.getAppointments().stream()
                        .map(appt -> new Appointment(
                                new Title(appt.getTitle() + " (" + patient.getName() + ", " + patient.getNric() + ")"),
                                appt.getDateTime()
                        )))
                .filter(appointment -> appointment.getDateTime().isAfter(LocalDateTime.now()))
                .sorted()
                .toList();

        return FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(upcomingAppointments)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DoctorBase)) {
            return false;
        }

        DoctorBase otherDoctorBase = (DoctorBase) other;
        return patients.equals(otherDoctorBase.patients);
    }

    @Override
    public int hashCode() {
        return patients.hashCode();
    }
}
