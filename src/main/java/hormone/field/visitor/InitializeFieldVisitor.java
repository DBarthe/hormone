package hormone.field.visitor;

import hormone.Model;
import hormone.annotation.Column;
import hormone.annotation.NoneClass;
import hormone.exception.MappingException;
import hormone.field.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * Visiteur dont la responsabilité est de configurer un Field en faisant de la reflection et en regardant les annotations.
 */
public class InitializeFieldVisitor implements FieldVisitor {

    private Class<? extends Model> modelType;
    private Column column;
    private java.lang.reflect.Field reflectField;

    public InitializeFieldVisitor(Class<? extends Model> modelType, Column column, java.lang.reflect.Field reflectField) {
        this.modelType = modelType;
        this.column = column;
        this.reflectField = reflectField;
    }

    private void visitAbstract(AbstractField mappingField) {
        try {
            mappingField.setName(reflectField.getName());
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(reflectField.getName(), modelType);
            mappingField.setGetter(propertyDescriptor.getReadMethod());
            mappingField.setSetter(propertyDescriptor.getWriteMethod());
        } catch (IntrospectionException e) {
            throw new MappingException(e);
        }
    }

    protected void visitSingleColumn(SingleColumnField mappingField) {
        visitAbstract(mappingField);
        String columnName = column.name();
        if (columnName.length() == 0) {
            columnName = mappingField.getName();
        }
        mappingField.setColumnName(columnName);
    }

    @Override
    public void visit(SimpleField field) {
        visitSingleColumn(field);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(ToOne toOneField) {
        this.visitSingleColumn(toOneField);
        toOneField.setAssociateType((Class<? extends Model>) reflectField.getType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(InversedOne optionalInverseOne)  {
        visitAbstract(optionalInverseOne);
        optionalInverseOne.setAssociateType((Class<? extends Model>) reflectField.getType());
        hormone.annotation.InversedOne annotation = reflectField.getAnnotation(hormone.annotation.InversedOne.class);
        if (annotation == null) {
            throw new MappingException("Unreachable... GenericMapperCreator is fucked");
        }
        String ownedBy = annotation.ownedBy();
        try {
            optionalInverseOne.getAssociateType().getDeclaredField(ownedBy);
        } catch (NoSuchFieldException e) {
            throw new MappingException(
                String.format("Model %s has invalid InversedOne field %s cause ownedBy='%s' doesn't exist on opposite model %s",
                    modelType.getName(), reflectField.getName(), ownedBy, optionalInverseOne.getAssociateType().getName()
                ));
        }
        optionalInverseOne.setOwnedBy(ownedBy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(OneToMany oneToMany)  {
        visitAbstract(oneToMany);
        if (!List.class.isAssignableFrom(reflectField.getType())) {
            throw new MappingException("OneToMany column type must be List or superclass of List");
        }
        hormone.annotation.OneToMany annotation = reflectField.getAnnotation(hormone.annotation.OneToMany.class);
        if (annotation == null) {
            throw new MappingException("Unreachable... GenericMapperCreator is fucked");
        }
        oneToMany.setAssociateType(annotation.model());
        String targetedBy = annotation.targetedBy();
        try {
            oneToMany.getAssociateType().getDeclaredField(targetedBy);
        } catch (NoSuchFieldException e) {
            throw new MappingException(
                String.format("Model %s has invalid OneToMany field %s cause targetedBy='%s' doesn't exist on opposite model %s",
                    modelType.getName(), reflectField.getName(), targetedBy, oneToMany.getAssociateType().getName()
                ));
        }
        oneToMany.setTargetedBy(targetedBy);
    }

    @Override
    public void visit(EnumField field) {
        visitSingleColumn(field);
        try {
            field.setEnumClass(reflectField.getType());
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void visit(OptionalToOne optionalToOne) {
        this.visitSingleColumn(optionalToOne);
        visitAbstract(optionalToOne);
        hormone.annotation.ToOne annotation = reflectField.getAnnotation(hormone.annotation.ToOne.class);
        if (annotation == null) {
            throw new MappingException("Unreachable... GenericMapperCreator is fucked");
        }
        if (annotation.model() == NoneClass.class) {
            throw new MappingException(String.format("Model %s has invalid ToOne field %s : missing the 'model' property",
                modelType.getName(), reflectField.getName()
            ));
        }
        optionalToOne.setAssociateType(annotation.model());
    }

    @Override
    public void visit(OptionalInversedOne optionalInversedOne) {
        visitAbstract(optionalInversedOne);
        hormone.annotation.InversedOne annotation = reflectField.getAnnotation(hormone.annotation.InversedOne.class);
        if (annotation == null) {
            throw new MappingException("Unreachable... GenericMapperCreator is fucked");
        }
        if (annotation.model() == NoneClass.class) {
            throw new MappingException(String.format("Model %s has invalid InversedOne field %s : missing the 'model' property",
                modelType.getName(), reflectField.getName()
            ));
        }
        optionalInversedOne.setAssociateType(annotation.model());
        String ownedBy = annotation.ownedBy();
        try {
            optionalInversedOne.getAssociateType().getDeclaredField(ownedBy);
        } catch (NoSuchFieldException e) {
            throw new MappingException(
                String.format("Model %s has invalid InversedOne field %s cause ownedBy='%s' doesn't exist on opposite model %s",
                    modelType.getName(), reflectField.getName(), ownedBy, optionalInversedOne.getAssociateType().getName()
                ));
        }
        optionalInversedOne.setOwnedBy(ownedBy);
    }
}
