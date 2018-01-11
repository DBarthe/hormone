package hormone;

import java.util.ArrayList;
import java.util.List;

/**
 * This object holds session configuration (url, models to be mapped, autocommit mode...)
 */
public class SessionConfiguration {

    private String databaseURL = null;
    private List<Class<? extends Model>> models = new ArrayList<>();
    private boolean autoCommit = true;

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public List<Class<? extends Model>> getModels() {
        return models;
    }

    public void addModel(Class<? extends Model> model) {
        models.add(model);
    }

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
}
