package hormone.field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BooleanField extends SimpleField {

    @Override
    protected int getSqlType() {
        return Types.BOOLEAN;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        st.setBoolean(index, (Boolean) value);
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        return rs.getBoolean(index);
    }
}
