package hormone.functionnal.model.enum_class;

import hormone.annotation.Column;
import hormone.annotation.Table;
import hormone.Model;

@Table
public class Person extends Model {
    @Column
    Gender gender;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
