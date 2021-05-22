package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;
import javax.persistence.*;

public class AssociationDto implements Serializable {

    private Integer id;
    private String itemTwo;

    public AssociationDto() {}

    public AssociationDto(Integer idItemOne, String idItemTwo) {
        this.id = idItemOne;
        this.itemTwo = idItemTwo;
    }

    public Integer getItemOne() {return id;}

    public String getItemTwo() {return itemTwo;}

    @Override
    public String toString() {
        return "AssociationDto{" +
                "id=" + id +
                ", itemTwo='" + itemTwo + '\'' +
                '}';
    }
}