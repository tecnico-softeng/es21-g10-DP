package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "association")
public class Association implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String itemTwo;

    @ManyToOne
    @JoinColumn(name="ITEM_ID")
    private Item item;

    public Association() {}

    public Association(Integer idItemOne, String idItemTwo) {
        this.id = idItemOne;
        this.itemTwo = idItemTwo;
    }

    public Integer getItemOne() {return id;}

    public String getItemTwo() {return itemTwo;}

    @Override
    public String toString() {
        return "Association{" +
                "id=" + id +
                ", itemTwo='" + itemTwo + '\'' +
                '}';
    }
}