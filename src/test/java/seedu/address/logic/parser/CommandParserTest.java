package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HEADING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UPDATE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEvents.MEETING_WITH_ALICE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DisplayCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.ScheduleAddCommand;
import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.commands.ScheduleDeleteCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UpdateCommand;
import seedu.address.logic.commands.UpdateCommand.UpdatePersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.event.Event;
import seedu.address.model.event.Heading;
import seedu.address.model.person.AddressContainsKeywordsPredicate;
import seedu.address.model.person.DescriptionContainsKeywordsPredicate;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.KinContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.testutil.EventBuilder;
import seedu.address.testutil.EventUtil;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.UpdatePersonDescriptorBuilder;

public class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }
    @Test
    public void parseCommand_addSchedule() throws Exception {
        Event event = new EventBuilder().build();
        ScheduleAddCommand command = (ScheduleAddCommand) parser.parseCommand(ScheduleCommand.COMMAND_WORD + " "
                + EventUtil.getScheduleAddCommand(event));
        assertEquals(new ScheduleAddCommand(event), command);
    }

    @Test
    public void parseCommand_deleteSchedule() throws Exception {
        Heading heading = MEETING_WITH_ALICE.getHeading();
        ScheduleDeleteCommand command = (ScheduleDeleteCommand) parser
                .parseCommand(ScheduleCommand.COMMAND_WORD + " " + ScheduleDeleteCommand.COMMAND_WORD + " "
                + PREFIX_HEADING + MEETING_WITH_ALICE.getHeading());
        assertEquals(new ScheduleDeleteCommand(heading), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }
    @Test
    public void parseCommand_update() throws Exception {
        Person person = new PersonBuilder().build();
        UpdatePersonDescriptor descriptor = new UpdatePersonDescriptorBuilder(person).build();
        UpdateCommand command = (UpdateCommand) parser.parseCommand(UpdateCommand.COMMAND_WORD + " "
                + PREFIX_UPDATE + ALICE.getName() + " " + PersonUtil.getUpdatePersonDescriptorDetails(descriptor));
        assertEquals(new UpdateCommand(ALICE.getName(), descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        assertTrue(parser.parseCommand(FindCommand.COMMAND_WORD + " n/foo") instanceof FindCommand);
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("k"));
        AddressContainsKeywordsPredicate addressPredicate = new AddressContainsKeywordsPredicate(Arrays.asList("a"));
        PhoneContainsKeywordsPredicate phonePredicate = new PhoneContainsKeywordsPredicate(Arrays.asList("1"));
        EmailContainsKeywordsPredicate emailPredicate = new EmailContainsKeywordsPredicate(Arrays.asList("e"));
        TagContainsKeywordsPredicate tagPredicate = new TagContainsKeywordsPredicate(Arrays.asList("t"));
        KinContainsKeywordsPredicate kinPredicate = new KinContainsKeywordsPredicate(Arrays.asList("k"));
        DescriptionContainsKeywordsPredicate descriptionPredicate =
            new DescriptionContainsKeywordsPredicate(Arrays.asList("d"));
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + "n/k a/a k/k t/t d/d p/1 e/e");
        assertEquals(new FindCommand(namePredicate, phonePredicate, addressPredicate, emailPredicate,
            tagPredicate, kinPredicate, descriptionPredicate), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_undo() throws Exception {
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD + " 3") instanceof UndoCommand);
    }
    @Test
    public void parseCommand_redo() throws Exception {
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD) instanceof RedoCommand);
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD + " 3") instanceof RedoCommand);
    }

    @Test
    public void parseCommand_display() throws ParseException {
        List<String> keywords = Arrays.asList("foo");
        DisplayCommand command = (DisplayCommand) parser.parseCommand(
                DisplayCommand.COMMAND_WORD + " " + String.join(" ", keywords));
        assertEquals(new DisplayCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }
}
