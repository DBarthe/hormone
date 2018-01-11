package hormone.functionnal;


import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import hormone.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is an abstract test class that provides a fresh temporary sqlite database encapsulated in an hormone.Session
 */
public abstract class AbstractTestWithDatabase {

    protected Session session;

    public final TemporaryFolder folder = new TemporaryFolder();

    public final ExternalResource database = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+ folder.getRoot().getAbsolutePath() +"/test_db.db");
            session = new Session(connection);
        }

        @Override
        protected void after() {
            session.close();
        }
    };

    @Rule
    public RuleChain chain = RuleChain.outerRule(folder).around(database);

    protected void execute(String sql) throws SQLException {
        session.getConnection().createStatement().execute(sql);
    }

    protected ResultSet executeQuery(String sql) throws SQLException {
        return session.getConnection().createStatement().executeQuery(sql);
    }
}
