package hormone.exception;

import java.sql.SQLException;

public class JDBCException extends ORMException {
    public JDBCException(SQLException e) {
        super(e);
    }
}
