package hormone.field.class_finder;

import hormone.exception.MappingException;
import hormone.field.Field;

public class MultipleAssociationClassFinder implements FieldClassFinder {
    @Override
    public Class<? extends Field> apply(java.lang.reflect.Field reflectField) {

        if (reflectField.getAnnotation(hormone.annotation.OneToMany.class) != null) {
            return hormone.field.OneToMany.class;
        }
        else {
            throw new MappingException("Missing @OneToMany for column " + reflectField.getName());
        }
    }
}
