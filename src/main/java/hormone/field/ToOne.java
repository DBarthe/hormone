package hormone.field;

import hormone.Model;
import hormone.Session;
import hormone.exception.JDBCException;
import hormone.exception.MappingException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ToOne extends SingleColumnField {

    private Class<? extends Model> associateType;

    @Override
    public void bindValue(PreparedStatement st, Object value, int index, Session session) {
        try {
            if (value == null) {
                st.setNull(index,  Types.VARCHAR);
            }
            else {
                // TODO: implement string length verification for fkey

                // value is a key
                if (value instanceof String) {
                    st.setString(index, (String)value);
                }
                // value is an entity
                else if (value instanceof Model) {
                    st.setString(index, ((Model)value).getId());
                }
                else {
                    throw new MappingException("ToOne.bindValue() should receive values of type Model or String" +
                        "but got " + value.getClass().getName()
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
            Model associate = fkey == null ? null : fetch(fkey, owner, session);
            getSetter().invoke(owner, associate);
        } catch (SQLException e) {
            throw new JDBCException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    protected Model fetch(String fkey, Model owner, Session session) {
        return session.getMapper(associateType).find(fkey);
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
