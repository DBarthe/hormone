package hormone.field;

import hormone.exception.ValidationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class StringField extends SimpleField {

    /** the length of VARCHAR, or -1 if not limit */
    private int length = -1;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    protected int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        checkLength((String)value);
        st.setString(index, (String)value);
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        String value = rs.getString(index);
        checkLength(value);
        return value;
    }

    private void checkLength(String value) {
        if (getLength() != -1 && value.length() > getLength()) {
            throw new ValidationException(String.format("String '%s' is to long (%d) for VARCHAR(%d)",
                value, value.length(), getLength()));
        }
    }
}
