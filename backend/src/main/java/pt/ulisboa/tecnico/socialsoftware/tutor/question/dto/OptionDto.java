package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.OPTION_IS_NOT_ORDERED;

public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private boolean correct;
    private String content;
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        if (order == null) return;
        this.order = order;
    }

    public boolean isOrdered() {
        return this.order != null;
    }

    public OptionDto() {
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
        if (option.isOrdered()) setOrder(option.getOrderPos());
    }

    public OptionDto(CodeFillInOption option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return true;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "OptionDto{" +
                "id=" + id +
                ", correct=" + correct +
                ", content='" + content + '\'' +
                '}';
    }
}