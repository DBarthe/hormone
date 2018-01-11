package hormone.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {
    /**
     * The name of the column in the database.
     * If left blank, the column name must match the field name.
     * @optional
     */
    String name() default "";
}
