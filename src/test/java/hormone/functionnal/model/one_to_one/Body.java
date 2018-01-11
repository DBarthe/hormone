package hormone.functionnal.model.one_to_one;

import hormone.annotation.Column;
import hormone.annotation.InversedOne;
import hormone.annotation.Table;
import hormone.Model;

@Table
public class Body extends Model {
    @Column
    @InversedOne(ownedBy = "body")
    Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
