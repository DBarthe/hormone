package hormone.field;

import hormone.Model;
import hormone.Session;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Un object Field est responsable du mapping entre un champs java et sa correspondance en base de données.
 *
 * Il contient les informations de bases :
 *      - le nom du champs java
 *      - le nombre de colonnes dans la table: 0 pour certaine associations, 1 la pluspart du temps, plus pour des objets (Point(x,y) par exemple)
 *      - le noms des éventuelles colonnes
 *      - le getter et le setter de l'attribut java
 *
 * Il a la responsabilité d'écrire la valeur dans un placeholder de preparedstatement :
 *      - bindValue
 *      - bindInstanceValue
 *
 * D'extraire une valeur depuis un result set
 *      - hydrate (uniquement les données directement accèsible)
 *      - resolve (résoud les associations)
 *
 * Pour la configuration des Field on utilise un visiteur, d'où la méthode : accept()
 *
 *
 * Il y a toute une hierarchie de Fields... Integer, Duration, Enum, Associations...
 */
public interface Field {

    /**
     * @return the field name
     */
    String getName();

    /**
     * Set the field name
     * @param name
     */
    void setName(String name);

    /**
     * @return the number of columns occupied by the field in the database
     * Could be zero with association types
     */
    int getColumnSpan();


    /**
     * @return the java field getter
     */
    Method getGetter();

    /**
     * @param getter the java field getter
     */
    void setGetter(Method getter);

    /**
     * @return the java field setter
     */
    Method getSetter();

    /**
     * @param setter the java field setter
     */
    void setSetter(Method setter);


    /**
     * @return the names of columns occupied by the field in the database
     * Could be empty with association types
     */
    List<String> getColumnNames();

    /**
     * Bind a value to a prepared statement
     * @param st the statement
     * @param value the value to bind
     * @param index the index from where to replace placeholder in the statement
     * @param session the current session
     */
    void bindValue(PreparedStatement st, Object value, int index, Session session);

    /**
     * Like bindValue but get the value from the instance parameter
     * @param st the statement
     * @param owner the instance form where to get the value
     * @param index the index from where to replace placeholder in the statement
     * @param session the current session
     */
    void bindInstanceValue(PreparedStatement st, Model owner, int index, Session session);

    /**
     * This is the first part of the two-phase extracting.
     * It is responsible of extracting direct value without fetching any association.
     * Then the hydrated instance will be store into the cache repository before
     * to start the second phase.
     * @param rs the result set
     * @param owner the object to hydrate
     * @param index the index from where to hydrate value in the result set's row
     * @param session the current session
     */
    void hydrate(ResultSet rs, Model owner, int index, Session session);

    /**
     * This is the second part of the two-phase extracting.
     * It is responsible of resolving associations between models after the instance
     * has been cached in the repository. This very useful to avoid dependency cycling
     * @param rs the result set
     * @param owner the object in which to resolve associations
     * @param index the index from where to hydrate value in the result set's row
     * @param session the current session
     */
    void resolve(ResultSet rs, Model owner, int index, Session session);

    void accept(FieldVisitor visitor);
}
