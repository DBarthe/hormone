package hormone.functionnal.model.one_to_one;

import hormone.annotation.Column;
import hormone.annotation.Table;
import hormone.annotation.ToOne;
import hormone.Model;

@Table
public class Person extends Model {
    @Column
    String name;

    @Column
    @ToOne
    Body body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
