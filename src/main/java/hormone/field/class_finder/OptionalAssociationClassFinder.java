package hormone.field.class_finder;

import hormone.exception.MappingException;
import hormone.field.Field;

public class OptionalAssociationClassFinder implements FieldClassFinder {
    @Override
    public Class<? extends Field> apply(java.lang.reflect.Field reflectField) {

        if (reflectField.getAnnotation(hormone.annotation.ToOne.class) != null) {
            return hormone.field.OptionalToOne.class;
        }
        else if (reflectField.getAnnotation(hormone.annotation.InversedOne.class) != null) {
            return hormone.field.OptionalInversedOne.class;
        }
        else {
            throw new MappingException("Missing @ToOne or @InversedOne for column " + reflectField.getName());
        }
    }
}
