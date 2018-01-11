package hormone.field;

import java.sql.*;

public class TimestampField extends SimpleField {

    @Override
    protected int getSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        st.setTimestamp(index, new Timestamp(((java.util.Date)value).getTime()));
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        return rs.getTimestamp(index);
    }
}
