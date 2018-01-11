package hormone;

import java.util.List;

/**
 * The interface for all data mappers.
 * Actually there is only one implementation : GenericMapper
 */
public interface Mapper<T extends Model> {

    /**
     * Find an object by its id key
     */
    T find(String id);

    /**
     * Select all the table
     */
    List<T> findAll();

    /**
     * Find one object matching a simple criteria (1 field only)
     */
    T findOneBy(String fieldName, Object value);

    /**
     * Find a list of object matching a simple criteria (1 field only)
     */
    List<T> findBy(String fieldName, Object value);

    /**
     * Make an object persistent (equivalent to insert in SQL).
     * If the object id is null, it is generated on the fly.
     */
    void persist(T model);

    /**
     * Update an persistent object
     */
    void update(T model);

    /**
     * If the object is transient (not persisted) it does a persists.
     * If the object is persisted already, it does an update;
     */
    void save(T model);

    /**
     * Count the number of rows in the table
     */
    int count();

    /**
     * Look-up in the cache repository (identity map) and save all the dirty object.
     * You still need to commit if autoCommit is false.
     */
    void flush();

    /**
     * Remove the object from the cache repository.
     */
    void evict(String id);

    /**
     * Remove the object from the cache repository.
     */
    default void evict(T model) { evict(model.getId()); }
}
