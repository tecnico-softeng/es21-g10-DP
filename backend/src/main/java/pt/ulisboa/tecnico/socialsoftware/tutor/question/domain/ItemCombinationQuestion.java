package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails {

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER)
    private final List<Item> items = new ArrayList<>();

    public ItemCombinationQuestion() {super();}

    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto questionDto) {
        super(question);
        setItems(questionDto.getItems());
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        if (items.size() < 2) {
            throw new TutorException(AT_LEAST_TWO_ITEMS_NEEDED);
        }

        boolean exception = true;
        for (ItemDto itemDto : items) {
            if (itemDto.getConnections().size() != 0) {
                exception = false;
                break;
            }
        }
        if (exception) {
            throw new TutorException(NO_EXISTING_CONNECTION);
        }

        int index = 0;
        for (ItemDto itemDto : items) {
            if (itemDto.getId() == null) {
                itemDto.setSequence(index++);
                new Item(itemDto).setQuestionDetails(this);
            } else {
                Item item = getItems()
                        .stream()
                        .filter(op -> op.getId().equals(itemDto.getId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(ITEM_NOT_FOUND, itemDto.getId()));

                item.setContent(itemDto.getContent());

                for (ItemDto possibleConnection : items) {
                    if (possibleConnection.getId() != null && itemDto.checkConnection(possibleConnection.getContent())) {
                        item.addConnections(possibleConnection.getContent());
                    }
                }
            }
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Integer getCorrectItemId() {
        return this.getItems().stream()
                .findAny()
                .map(Item::getId)
                .orElse(null);
    }

    public void update(ItemCombinationQuestionDto questionDetails) {
        setItems(questionDetails.getItems());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() { return null; }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitItems(Visitor visitor) {
        for (Item item : this.getItems()) {
            item.accept(visitor);
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (Item item : this.items) {
            item.remove();
        }
        this.items.clear();
    }

    @Override
    public String toString() {
        return "ItemCombinationQuestion{" +
                "items=" + items +
                '}';
    }

    public static String convertSequenceToLetter(Integer correctAnswer) {
        return correctAnswer != null ? Character.toString('A' + correctAnswer) : "-";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        var result = this.items
                .stream()
                .filter(x -> selectedIds.contains(x.getId()))
                .map(x -> convertSequenceToLetter(x.getSequence()))
                .collect(Collectors.joining("|"));
        return !result.isEmpty() ? result : "-";
    }
}
