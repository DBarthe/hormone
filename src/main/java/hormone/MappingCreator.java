package hormone;

import hormone.annotation.Column;
import hormone.annotation.Table;
import hormone.exception.MappingException;
import hormone.exception.ORMException;
import hormone.field.*;
import hormone.field.class_finder.FieldClassFinder;
import hormone.field.class_finder.MultipleAssociationClassFinder;
import hormone.field.class_finder.OptionalAssociationClassFinder;
import hormone.field.class_finder.SingleAssociationClassFinder;
import hormone.field.visitor.InitializeFieldVisitor;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

/**
 * Generate a GenericMapper from a model class by reading annotations and using reflection
 * This is all the introspective part of the ORM.
 * It also hardly depends on the Field type hierarchy and on InitializeFieldVisitor
 */
public class MappingCreator {

    private Class<? extends Model> type;

    /** Map a Java type to an FieldClassFinder */
    private static final Map<Class, FieldClassFinder> fieldMap = new HashMap<>();
    static {
        fieldMap.put(String.class, f -> StringField.class);
        fieldMap.put(Integer.class, f -> IntegerField.class);
        fieldMap.put(Float.class, f -> FloatField.class);
        fieldMap.put(Boolean.class, f -> BooleanField.class);
        fieldMap.put(Duration.class, f -> DurationField.class);
        fieldMap.put(Date.class, f -> TimestampField.class);
        fieldMap.put(Model.class, new SingleAssociationClassFinder());
        fieldMap.put(List.class, new MultipleAssociationClassFinder());
        fieldMap.put(Enum.class, f -> EnumField.class);
        fieldMap.put(Optional.class, new OptionalAssociationClassFinder());
    }

    /**
     * Build and return a generic mapper, discovering all the mapping using java annotations.
     */
    public <T extends Model> Mapping create(Class<T> type) {
        this.type = type;
        return discoverMapping();
    }

    private Mapping discoverMapping() {
        String tableName = type.getAnnotation(Table.class).name();
        if (tableName.length() == 0) {
            tableName = type.getSimpleName();
        }
        Mapping mapping = new Mapping(tableName);

        getAllFields().forEach(field -> {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                mapping.addField(parseColumnMapping(field, column));
            }
        });

        return mapping;
    }

    @SuppressWarnings("unchecked")
    private Field parseColumnMapping(java.lang.reflect.Field reflectField, Column column) {

        return fieldMap.entrySet()
            .stream()
            .filter(e -> e.getKey().isAssignableFrom(reflectField.getType()))
            .map(Map.Entry::getValue)
            .findFirst()
            .map(fieldClassFinder -> {
                try {
                    Class<? extends Field> fieldClass = fieldClassFinder.apply(reflectField);
                    Field field = fieldClass.newInstance();
                    field.accept(new InitializeFieldVisitor(type, column, reflectField));
                    return field;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new MappingException(e);
                }
            })
            .orElseThrow(() -> new ORMException("Ce type n'est pas implémenté par l'ORM. Demander à barth pour ajout."));
    }

    private Stream<java.lang.reflect.Field> getAllFields() {
        Stream<java.lang.reflect.Field> fieldStream = Arrays.stream(type.getDeclaredFields());
        return getInheritedFields(type.getSuperclass(), fieldStream);
    }

    private Stream<java.lang.reflect.Field> getInheritedFields(Class superclass, Stream<java.lang.reflect.Field> accumulatorStream) {
        // Terminal recursion
        if (superclass == null) {
            return accumulatorStream;
        }
        else {
            accumulatorStream = Stream.concat(accumulatorStream,  Arrays.stream(superclass.getDeclaredFields()));
            return getInheritedFields(superclass.getSuperclass(), accumulatorStream);
        }
    }
}
