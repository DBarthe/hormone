package hormone.functionnal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import hormone.functionnal.model.one_to_one.Body;
import hormone.functionnal.model.one_to_one.Person;

import java.sql.SQLException;

/**
 * Test one-to-one associations
 */
public class OneToOneTest extends hormone.functionnal.AbstractTestWithDatabase {

    @Before
    public void initPersonBody() throws SQLException {
        execute("CREATE TABLE body ( id VARCHAR(255) PRIMARY KEY )");
        execute("CREATE TABLE person ( id VARCHAR(255) PRIMARY KEY, name VARCHAR(50), body VARCHAR(255) REFERENCES body)");
        session.addModel(Person.class);
        session.addModel(Body.class);
    }

    @Test
    public void findOwnerFirst() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        execute("INSERT INTO person (id, name, body) VALUES ('p1', 'jaques', 'b1')");
        Person p1 = session.getMapper(Person.class).find("p1");
        Body b = p1.getBody();
        assertNotNull(b);
        Body b1 = session.getMapper(Body.class).find("b1");
        assertSame(b, b1);
        assertSame(p1.getBody(), b);
    }

    @Test
    public void findInverseFirst() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        execute("INSERT INTO person (id, name, body) VALUES ('p1', 'jaques', 'b1')");
        Body b1 = session.getMapper(Body.class).find("b1");
        Person p = b1.getPerson();
        assertNotNull(p);
        Person p1 = session.getMapper(Person.class).find("p1");
        assertSame(p, p1);
        assertSame(b1.getPerson(), p);
    }

    @Test
    public void findInverseNull() throws SQLException {
        execute("INSERT INTO person (id, name, body) VALUES ('p1', 'jaques', NULL)");
        Person p1 = session.getMapper(Person.class).find("p1");
        assertNull(p1.getBody());
    }

    @Test
    public void findOwnerNull() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        Body b1 = session.getMapper(Body.class).find("b1");
        assertNull(b1.getPerson());
    }

    @Test
    public void persistOwnerWithInverseNull() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        Person p = new Person();
        p.setId("p1");
        p.setName("bob");
        p.setBody(null);
        session.getMapper(Person.class).persist(p);
        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = 'p1' AND name = 'bob' AND body IS NULL")
                .getInt(1)
        );
    }

    @Test
    public void persistOwnerWithPersistedInverse() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        Body b = session.getMapper(Body.class).find("b1");
        Person p = new Person();
        p.setId("p1");
        p.setName("bob");
        p.setBody(b);
        session.getMapper(Person.class).persist(p);
        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = 'p1' AND name = 'bob' AND body = 'b1'")
                .getInt(1)
        );
    }

    @Test
    public void persistOwnerAndInverse() throws SQLException {
        Body b = new Body();
        Person p = new Person();
        b.setId("b1");
        b.setPerson(p);
        p.setId("p1");
        p.setName("bob");
        p.setBody(b);
        session.getMapper(Body.class).persist(b);
        session.getMapper(Person.class).persist(p);
        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = 'p1' AND name = 'bob' AND body = 'b1'")
                .getInt(1)
        );
    }

    @Test
    public void updateOwnerChangeInverse() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        execute("INSERT INTO body (id) VALUES ('b2')");
        execute("INSERT INTO person (id, name, body) VALUES ('p1', 'bob', 'b1')");
        Body b1 = session.getMapper(Body.class).find("b1");
        Body b2 = session.getMapper(Body.class).find("b2");
        Person p1 = session.getMapper(Person.class).find("p1");

        p1.setBody(b2);
        b1.setPerson(null);
        session.getMapper(Person.class).update(p1);

        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = 'p1' AND name = 'bob' AND body = 'b2'")
                .getInt(1)
        );
    }

    @Test
    public void updateOwnerSetNull() throws SQLException {
        execute("INSERT INTO body (id) VALUES ('b1')");
        execute("INSERT INTO person (id, name, body) VALUES ('p1', 'bob', 'b1')");
        Body b1 = session.getMapper(Body.class).find("b1");
        Person p1 = session.getMapper(Person.class).find("p1");

        p1.setBody(null);
        b1.setPerson(null);
        session.getMapper(Person.class).update(p1);

        assertEquals(1,
            executeQuery("SELECT count(*) FROM person WHERE id = 'p1' AND name = 'bob' AND body IS NULL")
                .getInt(1)
        );
    }

}
