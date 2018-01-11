package hormone.field;

import hormone.exception.ORMException;
import hormone.field.visitor.FieldVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Map un champs Java de type ENUM vers un champs VARCHAR en base de données
 */
public class EnumField extends SimpleField {

    private Class<?> enumClass;
    private Method toStringMethod;
    private Method ofStringMethod;

    @Override
    protected int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    protected void doBind(PreparedStatement st, Object value, int index) throws SQLException {
        try {
            st.setString(index, (String) toStringMethod.invoke(value));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new ORMException("Impossible d'utiliser la methode name() de l'object enum : " + e.getMessage());
        }
    }

    @Override
    protected Object doExtract(ResultSet rs, int index) throws SQLException {
        try {
            return ofStringMethod.invoke(null, enumClass, rs.getString(index));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new ORMException("Impossible d'utiliser la methode valueOf() de la classe enum :" + e.getMessage());
        }
    }

    @Override
    public void accept(FieldVisitor visitor) {
        visitor.visit(this);
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class enumClass) throws NoSuchMethodException {
        this.enumClass = enumClass;
        this.toStringMethod = Enum.class.getMethod("name", (Class<?>[]) null);
        this.ofStringMethod = Enum.class.getMethod("valueOf", Class.class, String.class);
    }


}
