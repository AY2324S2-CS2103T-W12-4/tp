package seedu.address.logic.commands;

import java.util.Arrays;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.AddressContainsKeywordsPredicate;
import seedu.address.model.person.DescriptionContainsKeywordsPredicate;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.KinContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Finds and lists all persons in address book whose name contains any of the
 * argument keywords.
 * Keyword matching is case insensitive.
 * Optionally can use parameters to search for specific fields as well.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) and optionally by additional parameters and \n"
            + "displays them as a list with index numbers.\n"
            + "Parameters: n/ KEYWORD [OPTIONAL PARAMETER] [OPTIONAL KEYWORD]...\n"
            + "Example: " + COMMAND_WORD + " n/alice n/bob p/1 e/nus a/clementi t/friends k/amy d/colleague\n";

    private final List<Predicate<Person>> predicates;

    public FindCommand(NameContainsKeywordsPredicate namePredicate, PhoneContainsKeywordsPredicate phonePredicate,
            AddressContainsKeywordsPredicate addressPredicate, EmailContainsKeywordsPredicate emailPredicate,
            TagContainsKeywordsPredicate tagPredicate, KinContainsKeywordsPredicate kinPredicate,
            DescriptionContainsKeywordsPredicate descriptionPredicate) {
        predicates = Arrays.asList(namePredicate, phonePredicate, addressPredicate,
                emailPredicate, tagPredicate, kinPredicate, descriptionPredicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Optional<Predicate<Person>> combinedPredicate = predicates.stream().reduce(Predicate::and);
        if (combinedPredicate.isPresent()) {
            model.updateFilteredPersonList(combinedPredicate.get());
        }

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicates.equals(otherFindCommand.predicates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicates", predicates)
                .toString();
    }
}
