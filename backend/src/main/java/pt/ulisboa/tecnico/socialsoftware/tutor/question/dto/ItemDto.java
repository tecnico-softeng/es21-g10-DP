package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Association;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ITEM_NOT_FOUND;

public class ItemDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private String content;
    private final List<AssociationDto> connections = new ArrayList<>();

    public ItemDto() {
    }

    public ItemDto(Item item) {
        this.id = item.getId();
        this.sequence = item.getSequence();
        this.content = item.getContent();
        setAssociations(item.getConnections());
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

    public String getContent() {
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<AssociationDto> getConnections() {return connections;}

    public void setAssociations(List<Association> associations) {
        if (associations != null) {
            for (Association connection : associations) {
                addConnections(connection.getItemTwo());
            }
        }
    }

    public void addConnections(String connection) {connections.add(new AssociationDto(this.id, connection));}

    public boolean checkConnection(String connection) {
        for (AssociationDto connections : getConnections()) {
            if (connections.getItemTwo().equals(connection)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", connections=" + connections +
                '}';
    }
}