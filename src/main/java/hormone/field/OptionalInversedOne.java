package hormone.field;

import hormone.*;
import hormone.exception.MappingException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.function.Supplier;

public class OptionalInversedOne extends AbstractInversed {

    @SuppressWarnings("unchecked")
    @Override
    public void resolve(ResultSet rs, Model owner, int index, Session session) {
        try {
            Supplier<Optional<Model>> factory = () ->
                OptionalImpl.ofNullable(session.getMapper(associateType).findOneBy(inversedBy, owner.getId()));
            Optional<Model> associate = (Optional<Model>)(new Proxy(Optional.class, factory)).getInstance();
            getSetter().invoke(owner, associate);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void accept(FieldVisitor visitor) {
        visitor.visit(this);
    }

    public Class<? extends Model> getAssociateType() {
        return associateType;
    }

    public void setAssociateType(Class<? extends Model> associateType) {
        this.associateType = associateType;
    }

    public String getOwnedBy() { return getInversedBy(); }
    public void setOwnedBy(String ownedBy) {
        setInversedBy(ownedBy);
    }
}
