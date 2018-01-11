package hormone.field;

import hormone.Model;
import hormone.Session;
import hormone.exception.MappingException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;

public class InversedOne extends AbstractInversed {

    @Override
    public void resolve(ResultSet rs, Model owner, int index, Session session) {
        try {
            Model associate = session.getMapper(associateType).findOneBy(inversedBy, owner.getId());
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
