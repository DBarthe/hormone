# hormone
a lightweight java ORM made at school


Features :
* Define mapping with plain java classes and annotation hints
* Cache data objects in a identity map
* Garbage collect cached objects using weak references
* Lazy loading of associations using a virtual proxy
* Primitives types support :
  * integer
  * string
  * date
  * timestamp
  * duration
  * enum
* Association types :
  * to-one
  * one-to-one
  * one-to-many
* In one-to-one associations, Optional can used in place of null pointers (thus enabling lazy loading on both side)
* Load the mapping once, instantiate many sessions
* Create multiple sessions for concurrent database access
* Tunable database synchronisation (auto-commit, manual commit)
* Domain layer can explicitly tag data objects as 'dirty', then the orm can later flush the modified objects implicitly.


## Examples

Model your data:
```java
@Table
class Person extends Model {
    @Column
    String firstName;

    @Column
    String lastName;

    @Column
    @ToOne(model = Person.class)
    Optional<Person> parent = OptionalImpl.empty();

    @Column
    @OneToMany(model = Person.class, targetedBy = "parent")
    List<Person> children = new ArrayList<>();
}
```


Initialisation:
```java
// Define a SessionConfiguration
SessionConfiguration configuration = new SessionConfiguration();
configuration.setDatabaseURL("jdbc:sqlite:db.sqlite");
configuration.setAutoCommit(false);
configuration.addModel(Person.class);

// Create the sessionFactory
SessionFactory sessionFactory = new SessionFactory(configuration);

// Open a session
Session session = sessionFactory.create();

```

Select:
```java
List<Person> family = session.getMapper(Person.class).findBy("lastName", "Doe");
```

Insert:
```java
Person pepin = new Person("Pepin", "le bref");
session.getMapper(Person.class).persist(pepin);
session.commit();
```

Update:
```java
pepin.setFirstName("John");
session.getMapper(Person.class).update(pepin);
session.commit();
```


## Quick reference

Mapper interface:
```java
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
```







