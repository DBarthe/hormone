package hormone.annotation;

import hormone.Model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface OneToMany {
    String targetedBy();
    Class<? extends Model> model();
}
