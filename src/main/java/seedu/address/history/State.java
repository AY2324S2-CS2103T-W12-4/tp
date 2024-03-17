package seedu.address.history;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.Command;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;


/**
 * The `State` class represents a snapshot of the HAL9000 application's state at a specific point in time.
 * It contains information about the executed command and the list of tasks at that time.
 */
public class State {
    private final Command command;
    private final ReadOnlyAddressBook addressBook;
    private final ObservableList<Person> filteredPersons;

    /**
     * Constructs a new State object with the given command and task list.
     *
     * @param command         The command executed to reach this state.
     * @param addressBook     The list of tasks at this state.
     * @param filteredPersons The list of filtered persons fis
     */
    public State(Command command, ReadOnlyAddressBook addressBook, ObservableList<Person> filteredPersons) {
        this.command = command;
        this.addressBook = addressBook;
        this.filteredPersons = filteredPersons;
    }

    /**
     * Gets the list of tasks at this state.
     *
     * @return The list of tasks.
     */
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /**
     * Gets the command executed to reach this state.
     *
     * @return The command.
     */
    public Command getCommand() {
        return command;
    }
    /**
     * Gets the command executed to reach this state.
     *
     * @return The command.
     */
    public ObservableList<Person> getFilteredList() {
        return filteredPersons;
    }
}
