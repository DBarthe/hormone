package hormone.field;

import hormone.field.visitor.FieldVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FloatField extends SimpleField {

    @Override
    protected int getSqlType() {
        return Types.FLOAT;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        st.setFloat(index, (Float) value);
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        return rs.getFloat(index);
    }

    @Override
    public void accept(FieldVisitor visitor) {
        visitor.visit(this);
    }
}
