package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

public class OpenEndedQuestionDto extends QuestionDetailsDto {
    private String proposedAnswer;
    private int maxCharacters;

    public OpenEndedQuestionDto() {
    }

    public OpenEndedQuestionDto(OpenEndedQuestion question) {
        proposedAnswer = question.getProposedAnswer();
        maxCharacters = question.getMaxCharacters();
    }

    public String getProposedAnswer() {
        return "";
    }

    public void setProposedAnswer(String answer) {
        proposedAnswer = answer;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int number) {
        maxCharacters = number;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenEndedQuestion(question, this);
    }

    @Override
    public void update(OpenEndedQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenEndedQuestionDto{" +
            "proposedAnswer=" + proposedAnswer +
            ", maxCharacters=" + maxCharacters +
            '}';
    }
}
