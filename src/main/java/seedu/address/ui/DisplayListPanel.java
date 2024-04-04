package seedu.address.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;



/**
 * Panel containing the list of persons.
 */
public class DisplayListPanel extends UiPart<Region> {
    private static final String FXML = "DisplayListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(DisplayListPanel.class);

    @FXML
    private ListView<FieldDescription> displayListView;

    @FXML
    private TextArea descriptionTextArea;

    private Person person;

    private Runnable swapPanelList;

    private CommandBox.CommandExecutor commandExecutor;

    /**
     * Creates a {@code DisplayListPanel} with the given {@code ObservableList}.
     */
    public DisplayListPanel(Person person, CommandBox.CommandExecutor commandExecutor, Runnable swapPanelList) {
        super(FXML);
        this.person = person;
        this.commandExecutor = commandExecutor;
        this.swapPanelList = swapPanelList;

        DisplayPerson displayPerson = new DisplayPerson(person);
        displayListView.setCellFactory(listView -> new DisplayListPanel.PersonListViewCell());
        displayListView.setItems(displayPerson.getFieldDescriptions());
        descriptionTextArea.setText(person.getDescription().get().value);
        descriptionTextArea.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        descriptionTextArea.setOnKeyPressed(event -> handleDescriptionEntered(event.getCode()));

        Platform.runLater(() -> {
            descriptionTextArea.requestFocus();
            descriptionTextArea.end();
        });
    }

    @FXML
    private void handleDescriptionEntered(KeyCode keyCode) {

        if (keyCode != KeyCode.ENTER) {
            return;
        }

        String descriptionText = descriptionTextArea.getText();
        if (descriptionText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(String.format("update u/%s d/%s", person.getName().fullName, descriptionText));
            swapPanelList.run();
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void setStyleToDefault() {
        descriptionTextArea.getStyleClass().remove(CommandBox.ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = descriptionTextArea.getStyleClass();

        if (styleClass.contains(CommandBox.ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(CommandBox.ERROR_STYLE_CLASS);
    }


    private class DisplayPerson {
        // Change method to utilise optionals instead of directly assuming the fields exist in the Person
        // person.getField().map(person -> String).orElse(DEFAULT)

        private static final String NAME_FIELD = "Name";
        private static final String PHONE_FIELD = "Phone";
        private static final String EMAIL_FIELD = "Email";
        private static final String ADDRESS_FIELD = "Address";
        private static final String NOK_FIELD = "Next of Kin";

        private final FieldDescription name;
        private final FieldDescription phone;
        private final FieldDescription email;
        private final FieldDescription address;
        private final FieldDescription nok;
        public DisplayPerson(Person person) {
            this.name = new FieldDescription(NAME_FIELD, person.getName().toString());
            this.email = new FieldDescription(EMAIL_FIELD, person.getEmail().toString());
            this.phone = new FieldDescription(PHONE_FIELD, person.getPhone().toString());
            this.address = new FieldDescription(ADDRESS_FIELD, person.getAddress().toString());
            this.nok = new FieldDescription(NOK_FIELD, person.getNextOfKin().toString());
        }

        private ObservableList<FieldDescription> getFieldDescriptions() {
            return FXCollections.observableArrayList(name, email, phone, address, nok);
        }
    }
    /**
     * Represents a field description containing a field property and a description property.
     */
    public class FieldDescription {
        private final String fieldProperty;
        private final String descriptionProperty;
        /**
         * Constructs a FieldDescription with the given field and description.
         *
         * @param field       The field property of the description.
         * @param description The description property of the field.
         */
        public FieldDescription(String field, String description) {
            fieldProperty = field;
            descriptionProperty = description;
        }


        public String getField() {
            return fieldProperty;
        }

        public String getDescription() {
            return descriptionProperty;
        }

    }


    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<FieldDescription> {
        @Override
        protected void updateItem(FieldDescription fieldDescription, boolean empty) {
            super.updateItem(fieldDescription, empty);

            if (empty || fieldDescription == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new DisplayCard(fieldDescription).getRoot());
            }
        }
    }
}
