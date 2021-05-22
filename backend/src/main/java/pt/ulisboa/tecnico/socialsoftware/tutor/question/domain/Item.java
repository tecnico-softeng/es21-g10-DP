package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssociationDto;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "items")
public class Item implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "item", fetch = FetchType.LAZY)
    private final List<Association> connections = new ArrayList<>();

    public Item() {
    }

    public Item(ItemDto item) {
        setSequence(item.getSequence());
        setContent(item.getContent());
        setAssociations(item.getConnections());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitItem(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public List<Association> getConnections() {return connections;}

    public void setAssociations(List<AssociationDto> associations) {
        if (associations != null) {
            for (AssociationDto connection : associations) {
                addConnections(connection.getItemTwo());
            }
        }
    }

    public void addConnections(String connection) {this.connections.add(new Association(this.id, connection));}

    public boolean checkConnection(String connection) {
        for (Association connections : getConnections()) {
            if (connections.getItemTwo().equals(connection)) {
                return true;
            }
        }
        return false;
    }

    public void setSequence(Integer sequence) {
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_ITEM);

        this.sequence = sequence;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new TutorException(INVALID_CONTENT_FOR_ITEM);

        this.content = content;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
        question.addItem(this);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", content='" + content + '\'' +
                ", connections=" + connections +
                ", question=" + questionDetails.getId() +
                '}';
    }

    public void remove() {
        this.questionDetails = null;
    }
}