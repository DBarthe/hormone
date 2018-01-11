package hormone.field;

import hormone.Model;
import hormone.Proxy;
import hormone.Session;
import hormone.exception.MappingException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.Supplier;

public class OneToMany extends AbstractInversed {

    @SuppressWarnings("unchecked")
    @Override
    public void resolve(ResultSet rs, Model owner, int index, Session session) {
        try {
            Supplier factory = () -> session.getMapper(associateType).findBy(getTargetedBy(), owner.getId());
            Proxy proxy = new Proxy(List.class, factory);
            List<? extends Model> associates = (List<? extends Model>) proxy.getInstance();
            getSetter().invoke(owner, associates);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void accept(FieldVisitor visitor) {
        visitor.visit(this);
    }

    public String getTargetedBy() {
        return getInversedBy();
    }
    public void setTargetedBy(String targetedBy) { setInversedBy(targetedBy); }
}
