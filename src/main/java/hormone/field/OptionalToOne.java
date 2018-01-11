package hormone.field;

import hormone.*;
import hormone.exception.JDBCException;
import hormone.exception.MappingException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.function.Supplier;

public class OptionalToOne extends SingleColumnField {

    private Class<? extends Model> associateType;

    @SuppressWarnings("unchecked")
    @Override
    public void bindValue(PreparedStatement st, Object value, int index, Session session) {
        try {
            if (value == null) {
                st.setNull(index,  Types.VARCHAR);
            }
            else {
                // value is a key
                if (value instanceof String) {
                    st.setString(index, (String)value);
                }
                // value is an optional
                else if (value instanceof Optional) {
                    Optional<? extends Model> optionalValue = (Optional<? extends Model>)value;
                    // could be there
                    if (optionalValue.isPresent()) {
                        st.setString(index, optionalValue.get().getId());
                    }
                    // could be nothing
                    else {
                        st.setNull(index,  Types.VARCHAR);
                    }
                }
                // value is a Model and not an optional (useful in findBy statement)
                else if (value instanceof Model) {
                    Model modelValue = (Model)value;
                    st.setString(index, modelValue.getId());
                }
                else {
                    throw new MappingException("ToOne.bindValue() should receive values of type OptionalImpl or String or Model" +
                        " but got " + value.getClass().getName()
                    );
                }
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void bindInstanceValue(PreparedStatement st, Model owner, int index, Session session) {
        try {
            bindValue(st, getGetter().invoke(owner), index, session);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void hydrate(ResultSet rs, Model owner, int index, Session session) {
        try {
            owner.setInternalField(getName(), rs.getString(index));
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void resolve(ResultSet rs, Model owner, int index, Session session) {
        try {
            String fkey = rs.getString(index);
            Optional<Model> associate = fkey == null ? OptionalImpl.empty() : fetch(fkey, owner, session);
            getSetter().invoke(owner, associate);
        } catch (SQLException e) {
            throw new JDBCException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Optional<Model> fetch(String fkey, Model owner, Session session) {
        Supplier<OptionalImpl<Model>> factory = () -> OptionalImpl.of(session.getMapper(associateType).find(fkey));
        Proxy proxy = new Proxy(Optional.class, factory);
        return (Optional<Model>) proxy.getInstance();
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
}
