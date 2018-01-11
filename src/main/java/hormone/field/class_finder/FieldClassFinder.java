package hormone.field.class_finder;

import hormone.field.Field;

/** A function that retrieves hormone.Field from a java.lang.reflect.Field */
@FunctionalInterface
public interface FieldClassFinder {
    Class<? extends Field> apply(java.lang.reflect.Field reflectField);
}
