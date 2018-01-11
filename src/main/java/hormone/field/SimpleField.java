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

/**
 * A field mapped to only one column
 */
public abstract class SimpleField extends SingleColumnField {

    @Override
    public void bindValue(PreparedStatement st, Object value, int index, Session session) {
        try {
            if (value == null) {
                st.setNull(index, getSqlType());
            }
            else {
                doBind(st, value, index);
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
            getSetter().invoke(owner, doExtract(rs, index));
        } catch (SQLException e) {
            throw new JDBCException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void resolve(ResultSet rs, Model owner, int index, Session session) {
        // nothing to do
    }

    protected abstract int getSqlType();
    protected abstract void doBind(PreparedStatement st, Object v, int index) throws SQLException;
    protected abstract Object doExtract(ResultSet rs, int index) throws SQLException;

    @Override
    public void accept(FieldVisitor visitor) {
        visitor.visit(this);
    }
}
