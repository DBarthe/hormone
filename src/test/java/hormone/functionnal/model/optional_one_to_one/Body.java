package hormone.functionnal.model.optional_one_to_one;

import hormone.Model;
import hormone.Optional;
import hormone.annotation.Column;
import hormone.annotation.InversedOne;
import hormone.annotation.Table;

@Table
public class Body extends Model {
    @Column
    @InversedOne(ownedBy = "body", model = Person.class)
    Optional<Person> person;

    public Optional<Person> getPerson() {
        return person;
    }

    public void setPerson(Optional<Person> person) {
        this.person = person;
    }
}
