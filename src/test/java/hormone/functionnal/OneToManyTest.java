package hormone.functionnal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import hormone.functionnal.model.one_to_many.Car;
import hormone.functionnal.model.one_to_many.Person;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test one-to-many associations
 */
public class OneToManyTest extends hormone.functionnal.AbstractTestWithDatabase {

    @Before
    public void initPersonCar() throws SQLException {
        execute("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(50))");
        execute("CREATE TABLE car (id VARCHAR(255) PRIMARY KEY, person VARCHAR(255) REFERENCES person)");

        session.addModel(Person.class);
        session.addModel(Car.class);

        execute("INSERT INTO person (id, name) VALUES ('p1', 'bob')");
        execute("INSERT INTO person (id, name) VALUES ('p2', 'jim')");
        execute("INSERT INTO person (id, name) VALUES ('p3', 'mik')");

        // bob cars
        execute("INSERT INTO car (id, person) VALUES ('c1', 'p1')");
        execute("INSERT INTO car (id, person) VALUES ('c2', 'p1')");
        execute("INSERT INTO car (id, person) VALUES ('c3', 'p1')");
        execute("INSERT INTO car (id, person) VALUES ('c4', 'p1')");

        // jim car
        execute("INSERT INTO car (id, person) VALUES ('c5', 'p2')");

        // ... mik has no car

        // orphan car
        execute("INSERT INTO car (id, person) VALUES ('c6', NULL)");
    }

    @Test
    public void findOneFirst() throws SQLException {
        Person bob = session.getMapper(Person.class).find("p1");
        assertEquals(4, bob.getCars().size());
        for (Car car : bob.getCars()) {
            assertSame(bob, car.getPerson());
        }

        Person jim = session.getMapper(Person.class).find("p2");
        assertEquals(1, jim.getCars().size());
        assertEquals("c5", jim.getCars().get(0).getId());

        Person mik = session.getMapper(Person.class).find("p3");
        assertEquals(0, mik.getCars().size());
    }

    @Test
    public void findManyFirst() throws SQLException {
        Car c1 = session.getMapper(Car.class).find("c1");
        assertEquals("bob", c1.getPerson().getName());
        assertTrue(c1.getPerson().getCars().contains(c1));
        Car c2 = session.getMapper(Car.class).find("c2");
        assertSame(c1.getPerson(), c2.getPerson());
        assertTrue(c2.getPerson().getCars().contains(c2));
        Car c6 = session.getMapper(Car.class).find("c6");
        assertNull(c6.getPerson());
    }

    @Test
    public void findAllMany() throws SQLException {
        List<Car> allCars = session.getMapper(Car.class).findAll();
        assertEquals(6, allCars.size());
        Set<Person> personSet = new HashSet<>();
        for (Car car : allCars) {
            if (car.getPerson() != null) {
                personSet.add(car.getPerson());
            }
        }
        assertEquals(2, personSet.size());
        assertTrue(personSet.contains(session.getMapper(Person.class).find("p1")));
        assertTrue(personSet.contains(session.getMapper(Person.class).find("p2")));
    }

    @Test
    public void attachCar() throws SQLException {
        Person jim = session.getMapper(Person.class).find("p2");
        Car orphan = session.getMapper(Car.class).find("c6");
        orphan.setPerson(jim);
        jim.getCars().add(orphan);
        session.getMapper(Car.class).update(orphan);
        assertEquals(1, executeQuery("SELECT count(*) FROM car WHERE id = 'c6' AND person = 'p2'").getInt(1));
    }

    @Test
    public void detachCar() throws SQLException {
        Person bob = session.getMapper(Person.class).find("p1");
        Car c1 = session.getMapper(Car.class).find("c1");
        bob.getCars().remove(c1);
        c1.setPerson(null);
        session.getMapper(Car.class).update(c1);
        assertEquals(1, executeQuery("SELECT count(*) FROM car WHERE id = 'c1' AND person IS NULL").getInt(1));
    }

    @Test
    public void persistCar() throws SQLException {
        Person bob = session.getMapper(Person.class).find("p1");
        Car car = new Car();
        car.setId("c7");
        car.setPerson(bob);
        session.getMapper(Car.class).persist(car);
        assertEquals(1, executeQuery("SELECT count(*) FROM car WHERE id = 'c7' AND person = 'p1'").getInt(1));
    }
}
