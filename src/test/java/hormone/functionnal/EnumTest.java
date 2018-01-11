package hormone.functionnal;

import org.junit.Before;
import org.junit.Test;
import hormone.functionnal.model.enum_class.Gender;
import hormone.functionnal.model.enum_class.Person;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Test enum field type mapping
 */
public class EnumTest extends AbstractTestWithDatabase {

    @Before
    public void initPerson() throws SQLException {
        execute("CREATE TABLE person ( id VARCHAR(255) PRIMARY KEY, gender VARCHAR(255))");
        session.addModel(Person.class);
    }

    @Test
    public void persist() throws SQLException {
        Person p = new Person();
        p.setId("1");
        p.setGender(Gender.MALE);
        session.getMapper(Person.class).persist(p);
        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = '1' AND gender = 'MALE'")
                .getInt(1)
        );
    }

    @Test
    public void select() throws SQLException {
        execute("INSERT INTO person (id, gender) values (2, 'FEMALE')");
        Person p = session.getMapper(Person.class).find("2");
        assertEquals(Gender.FEMALE, p.getGender());
    }
}
