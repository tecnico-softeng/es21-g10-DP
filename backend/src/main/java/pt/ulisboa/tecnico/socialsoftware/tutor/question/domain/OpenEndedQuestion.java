package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedQuestion extends QuestionDetails {
    // without this answers can only have 255 characters by default.
    @Column(columnDefinition = "TEXT")
    private String proposedAnswer;

    @Column
    private int maxCharacters;

    public OpenEndedQuestion() {
        super();
    }

    public OpenEndedQuestion(Question question, OpenEndedQuestionDto questionDto) {
        super(question);
        setProposedAnswer(questionDto.getProposedAnswer());
        setMaxCharacters(questionDto.getMaxCharacters());
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int number) {
        maxCharacters = number;
    }

    public String getProposedAnswer() {
        return proposedAnswer;
    }

    public void setProposedAnswer(String answer) {
        if(answer == null || answer.isEmpty())
        {
            throw new TutorException(ErrorMessage.NO_PROPOSED_ANSWER);
        }
        proposedAnswer = answer;
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null; //TODO
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null; //TODO
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null; //TODO
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null; //TODO
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenEndedQuestionDto(this);
    }

    public void update(OpenEndedQuestionDto openEndedQuestionDto) {
        setProposedAnswer(openEndedQuestionDto.getProposedAnswer());
        setMaxCharacters(openEndedQuestionDto.getMaxCharacters());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void delete() {
        super.delete();
        proposedAnswer = null;
        maxCharacters = 0;
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return null; //TODO
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null; //TODO
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

}
