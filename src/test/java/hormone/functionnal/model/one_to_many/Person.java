package hormone.functionnal.model.one_to_many;

import hormone.annotation.Column;
import hormone.annotation.OneToMany;
import hormone.annotation.Table;
import hormone.Model;

import java.util.List;

@Table
public class Person extends Model {
    @Column
    String name;

    @Column
    @OneToMany(targetedBy = "person", model = Car.class)
    List<Car> cars;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
