package hormone;

import hormone.exception.JDBCException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This object is responsible of creating a session object
 * It notably holds all the mappings for model classes listed in SessionConfiguration.getModels().
 * It is a very expensive object to create. Thus it has to be instantiated once. Then used each time we
 * need to create a new session.
 */
public class SessionFactory {

    /**
     * Holds the connection credentials, models to map, and various mode such as auto-commit
     */
    private SessionConfiguration configuration;
    private HashMap<Class<? extends Model>, Mapping> mappings = new HashMap<>();

    public SessionFactory(SessionConfiguration configuration) {
        this.configuration = configuration;
        createMappings();
    }

    public Session create() {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(configuration.getDatabaseURL());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JDBCException(e);
        }
        Session session = new Session(connection);

        for (Class<? extends Model> modelCls : configuration.getModels()) {
            session.addModel(modelCls, mappings.get(modelCls));
        }

        session.setAutoCommit(configuration.getAutoCommit());

        return session;
    }

    private void createMappings() {
        MappingCreator mappingCreator = new MappingCreator();
        for (Class<? extends Model> modelCls : configuration.getModels()) {
            mappings.put(modelCls, mappingCreator.create(modelCls));
        }
    }

}
