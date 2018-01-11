package hormone.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Table {
    /**
     * The name of the database table.
     * If left blank, database name has to match class name.
     */
    String name() default "";
}
