package hormone.annotation;

import hormone.Model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface InversedOne {
    String ownedBy();

    /**
     * The associate class. This class is inferred automatically if java field type is a subclass of Model.
     * It's required only if the java field type is java.util.OptionalImpl.
     */
    Class<? extends Model> model() default NoneClass.class;
}
