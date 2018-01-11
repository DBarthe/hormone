package hormone.field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;

public class DurationField extends SimpleField {

    @Override
    protected int getSqlType() {
        return Types.BIGINT;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        st.setLong(index, ((Duration) value).toMillis());
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        return Duration.ofMillis(rs.getLong(index));
    }
}
