package hormone.field;

import java.util.Collections;
import java.util.List;

public abstract class SingleColumnField extends AbstractField {

    private String columnName;

    @Override
    public int getColumnSpan() {
        return 1;
    }

    @Override
    public List<String> getColumnNames() {
        return Collections.singletonList(columnName);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
