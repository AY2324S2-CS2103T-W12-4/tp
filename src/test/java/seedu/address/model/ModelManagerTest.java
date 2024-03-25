package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static seedu.address.logic.commands.StartCommand.getStartCommand;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.CommandUtil.getCommandStub;
import static seedu.address.testutil.HistoryUtil.TYPICAL_SECOND_STATE;
import static seedu.address.testutil.HistoryUtil.TYPICAL_START_STATE;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.history.State;
import seedu.address.history.exceptions.HistoryException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equalsTest() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void initializationFailure() {
        assertDoesNotThrow(ModelHistoryFailureStub::new);
        Model modelInitFailure = new ModelHistoryFailureStub();
        State state = modelInitFailure.getCurrentState();
        assertEquals(state.getAddressBook(), SampleDataUtil.getSampleAddressBook());
    }

    @Test
    public void setFilteredPersonsListTest() {
        ArrayList<Person> newSource = new ArrayList<>();
        newSource.add(ALICE);
        newSource.add(BENSON);
        modelManager.setFilteredPersonsList(new FilteredList<>(FXCollections.observableList(newSource)),
                PREDICATE_SHOW_ALL_PERSONS);
        modelManager.getFilteredPersonList();
        assertEquals(modelManager.getFilteredPersonList(), FXCollections.observableList(newSource));
    }

    @Test
    public void getCurrentStateTest() {
        State currState = modelManager.getCurrentState();
        assertEquals(currState, new State(getStartCommand(),
                modelManager.getAddressBook(),
                modelManager.getFilteredPersonList(),
                modelManager.getFilteredPersonsListPredicate()
        ));
    }

    @Test
    public void restoreStateTest() {
        State prevState = modelManager.getCurrentState();
        modelManager.restoreState(TYPICAL_SECOND_STATE);
    }

    @Test
    public void updateStateSuccess() {
        assertDoesNotThrow(() -> modelManager.updateState(getCommandStub()));
    }

    @Test
    public void rollBackStateSuccess() {
        try {
            modelManager.updateState(getCommandStub());
        } catch (HistoryException e) {
            fail();
        }
        assertDoesNotThrow(() -> modelManager.rollBackState());
    }

    @Test
    public void rollBackStateFailure() {
        assertThrows(HistoryException.class, () -> modelManager.rollBackState());
    }

    @Test
    public void rollForwardStateSuccess() {
        try {
            modelManager.updateState(getCommandStub());
            modelManager.rollBackState();
        } catch (HistoryException e) {
            fail();
        }
        assertDoesNotThrow(() -> modelManager.rollForwardState());
    }

    @Test
    public void rollForwardStateFailure() {
        assertThrows(HistoryException.class, () -> modelManager.rollForwardState());
    }

    private static class ModelHistoryFailureStub extends ModelManager {
        @Override
        public ObservableList<Person> deepCopyFilteredPersonsList() throws IllegalValueException {
            throw new IllegalValueException("FAILURE TO CLONE");
        }
    }
}
