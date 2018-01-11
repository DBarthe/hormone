package hormone.functionnal.model.optional_one_to_one;

import hormone.Model;
import hormone.Optional;
import hormone.annotation.Column;
import hormone.annotation.Table;
import hormone.annotation.ToOne;

@Table
public class Person extends Model {
    @Column
    String name;

    @Column
    @ToOne(model = Body.class)
    Optional<Body> body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<Body> getBody() {
        return body;
    }

    public void setBody(Optional<Body> body) {
        this.body = body;
    }
}
