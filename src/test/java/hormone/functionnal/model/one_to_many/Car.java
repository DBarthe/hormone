package hormone.functionnal.model.one_to_many;

import hormone.annotation.*;
import hormone.Model;

@Table
public class Car extends Model {
    @Column
    @ToOne
    Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
