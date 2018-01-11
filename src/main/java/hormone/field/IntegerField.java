package hormone.field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerField extends SimpleField {

    @Override
    protected int getSqlType() {
        return Types.INTEGER;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        st.setInt(index, (Integer) value);
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }
}
