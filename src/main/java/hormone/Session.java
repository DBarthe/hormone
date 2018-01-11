package hormone;

import hormone.exception.JDBCException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The session hold a JDBC connection, all the mappers,
 * and handle a kind of UnitOfWork pattern using standard sql transaction.
 * A session is not thread safe. If you need concurrency, consider instantiate multiple sessions.
 */
public class Session {
    private Connection connection;
    private HashMap<Class<? extends Model>, Mapper<? extends Model>> mappers;

    public Session(Connection connection) {
        this.connection = connection;
        this.mappers = new HashMap<>();
    }

    /**
     * Return the inner jdbc connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Close the session definitely
     */
    public void close() {
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }


    /**
     * Create a mapper for the given model and mapping.
     * It is called by SessionFactory.
     */
    public void addModel(Class<? extends Model> type, Mapping mapping) {
        if (!mappers.containsKey(type)) {
            Mapper<? extends Model> mapper = new GenericMapper<>(type, this, mapping);
            mappers.put(type, mapper);
        }
    }

    /**
     * old version... kept for retro-compatibility with tests
     */
    @Deprecated
    public void addModel(Class<? extends Model> type) {
        addModel(type, new MappingCreator().create(type));
    }

    /**
     * Disable or enable auto-commit
     */
    public void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JDBCException(e);
        }
    }

    /**
     * Return true if autoCommit is enabled
     */
    public boolean getAutoCommit() {
        try {
            return connection.getAutoCommit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JDBCException(e);
        }
    }

    /**
     * Get the mapper instance corresponding to a model class
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> GenericMapper<T> getMapper(Class<T> type) {
        return (GenericMapper<T>) mappers.getOrDefault(type,null);
    }

    /**
     * Commit all the changes to database
     */
    public void commit() {
        System.out.println("1 commit effectué");
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JDBCException(e);
        }
    }

}
