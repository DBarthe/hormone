package hormone;

import hormone.exception.JDBCException;
import hormone.exception.MappingException;
import hormone.field.Field;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The Generic Mapper.
 *
 * It is initialized with a 'mapping' object which define how the class in mapped to the table.
 * It also has a Repository object with all the persistent objects it knows (identity map, weak reference).
 */
@SuppressWarnings("unchecked")
public class GenericMapper<T extends Model> implements Mapper<T> {

    private Class<T> type;
    private Session session;

    private Mapping mapping;
    private Repository repository;

    public GenericMapper(Class<T> type, Session session, Mapping mapping) {
        this.type = type;
        this.session = session;
        this.mapping = mapping;
        this.repository = new Repository(type);
    }

    Repository getRepository() {
        return repository;
    }
    public Mapping getMapping() { return mapping; }

    @Override
    public T find(String id)  {
        T find = (T) repository.get(id);
        if (find != null) {
            return find;
        }
        List<T> objects = findBy("id", id);
        return objects.size() == 0 ? null : objects.get(0);
    }

    @Override
    public List<T> findAll() {
        try {
            String sql = String.format("SELECT %s FROM %s",
                buildColumnList(mapping.getFields()),
                mapping.getTableName()
            );
            PreparedStatement st = session.getConnection().prepareStatement(sql);
            return find(st);
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public T findOneBy(String fieldName, Object value) {
        // look first in repository
        return streamRepositoryBy(fieldName, value).findFirst().orElseGet(() -> {
            // or else search in the database
            List<T> results = findBy(fieldName, value);
            return results.size() > 0 ? results.get(0) : null;
        });
    }

    private Stream<T> streamRepositoryBy(String fieldName, Object value) {
        Method getter = mapping.getField(fieldName).getGetter();
        return (Stream<T>) repository.stream().filter(model -> {
            try {
                if (model.hasInternalField(fieldName)) {
                    Object otherValue = model.getInternalField(fieldName);
                    if (value == null && otherValue != null) {
                        return false;
                    }
                    else if (value != null && value.equals(otherValue)) {
                        return true;
                    }
                }
                Object otherValue = getter.invoke(model);
                return (value == null && otherValue == null) || (value != null && value.equals(otherValue));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException(e);
            }
        });
    }

    @Override
    public List<T> findBy(String fieldName, Object value) {
        try {
            // todo: handle multi-columns fields
            String sql = String.format("SELECT %s FROM %s WHERE %s %s ?",
                buildColumnList(mapping.getFields()),
                mapping.getTableName(),
                mapping.getColumnNames(fieldName).get(0),
                (value == null ? "IS" : "=")
            );
            PreparedStatement st = session.getConnection().prepareStatement(sql);
            mapping.getField(fieldName).bindValue(st, value, 1, session);
            return find(st);
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    private List<T> find(PreparedStatement stmt) {
        System.out.println("1 find executé");
        try {
            ResultSet rows = stmt.executeQuery();
            ArrayList<T> objects =  new ArrayList<>();
            while (rows.next()) {
                String id = rows.getString(mapping.getColumnNames("id").get(0));
                T instance = (T) repository.get(id);
                if (instance != null) {
                    //hydrate(rows, instance);
                }
                else {
                    instance = createAndHydrate(rows);
                    repository.put(instance);
                    resolve(rows, instance);
                    instance.setDirty(false);
                }
                objects.add(instance);
            }
            return objects;

        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void persist(T model) {
        System.out.println("1 persist effectué");
        Objects.requireNonNull(model);
        if (model.getId() == null) {
            model.setGeneratedId();
        }
        try {
            List<Field> allFields = mapping.getFields();
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                mapping.getTableName(),
                buildColumnList(allFields),
                buildValuePlaceholderList(allFields)
            );
            PreparedStatement stmt = session.getConnection().prepareStatement(sql);
            int i = 1;
            for (Field field : allFields) {
                field.bindInstanceValue(stmt, model, i, session);
                i += field.getColumnSpan();
            }
            stmt.executeUpdate();
            repository.put(model);
            model.setDirty(false);
        }
        catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void update(T model) {
        System.out.println("1 update effectué");
        Objects.requireNonNull(model);
        try {
            List<Field> allFields = mapping.getFields();
            String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                mapping.getTableName(),
                buildSetValuePlaceholderList(allFields),
                mapping.getColumnNames("id").get(0));
            PreparedStatement stmt = session.getConnection().prepareStatement(sql);
            int i = 1;
            for (Field field : allFields) {
                field.bindInstanceValue(stmt, model, i, session);
                i += field.getColumnSpan();
            }
            mapping.getField("id").bindInstanceValue(stmt, model, i, session);
            stmt.executeUpdate();
            repository.put(model);
            model.setDirty(false);
        }
        catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    /**
     * Return the total number of rows in the table
     */
    @Override
    public int count() {
        try {
            String sql = String.format("SELECT count(*) FROM %s", mapping.getTableName());
            Statement stmt = session.getConnection().createStatement();
            stmt.execute(sql);
            return stmt.getResultSet().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JDBCException(e);
        }
    }

    @Override
    public void save(T model) {
        Objects.requireNonNull(model);
        if (model.getId() == null || repository.get(model.getId()) == null) {
            persist(model);
        }
        else {
            update(model);
        }
    }

    @Override
    public void flush() {
        System.out.println("1 flush demandé");
        repository.stream().filter(Model::isDirty).forEach(m -> this.update((T)m));
    }

    @Override
    public void evict(String id) {
        repository.remove(id);
    }

    private T createAndHydrate(ResultSet rows) {
        try {
            T instance = type.newInstance();
            hydrate(rows, instance);
            return instance;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException(e);
        }
    }

    private void hydrate(ResultSet rows, T instance) {
        int i = 1;
        for (Field field : mapping.getFields()) {
            field.hydrate(rows, instance, i, session);
            i += field.getColumnSpan();
        }
    }

    private void resolve(ResultSet rows, T instance) {
        int i = 1;
        for (Field field : mapping.getFields()) {
            field.resolve(rows, instance, i, session);
            i += field.getColumnSpan();
        }
    }

    private String buildColumnList(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Field f : fields) {
            for (String col : f.getColumnNames()) {
                if (!first) {
                    sb.append(",");
                } else {
                    first = false;
                }
                sb.append(col);
            }
        }
        return sb.toString();
    }

    private String buildSetValuePlaceholderList(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Field f : fields) {
            for (String col : f.getColumnNames()) {
                if (!first) {
                    sb.append(",");
                } else {
                    first = false;
                }
                sb.append(col).append("=?");
            }
        }
        return sb.toString();
    }

    private String buildValuePlaceholderList(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Field f : fields) {
            for (String col : f.getColumnNames()) {
                if (!first) {
                    sb.append(",");
                } else {
                    first = false;
                }
                sb.append("?");
            }
        }
        return sb.toString();
    }
}
