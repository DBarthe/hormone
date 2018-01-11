package hormone.field;

import hormone.Model;
import hormone.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInversed extends AbstractField {

    protected Class<? extends Model> associateType;
    protected String inversedBy;

    @Override
    public int getColumnSpan() {
        return 0;
    }

    @Override
    public List<String> getColumnNames() {
        return new ArrayList<>();
    }

    @Override
    public void bindValue(PreparedStatement st, Object value, int index, Session session) {
        // eventually trigger before or after statements
        // but it would be complicated...
        // - persist ? update ? or already up-to-date ?
        // - prevent infinite loops
        // - how to propagate the generated statements, respect execution order ?
    }

    @Override
    public void bindInstanceValue(PreparedStatement st, Model owner, int index, Session session) {
        // eventually trigger before or after statements
    }

    @Override
    public void hydrate(ResultSet rs, Model owner, int index, Session session) {
        // nothing to do
    }

    public Class<? extends Model> getAssociateType() {
        return associateType;
    }

    public void setAssociateType(Class<? extends Model> associateType) {
        this.associateType = associateType;
    }

    protected String getInversedBy() {
        return inversedBy;
    }

    protected void setInversedBy(String inversedBy) {
        this.inversedBy = inversedBy;
    }
}
