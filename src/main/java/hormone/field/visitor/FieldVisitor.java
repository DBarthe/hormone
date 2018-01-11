package hormone.field.visitor;

import hormone.field.*;

/**
 * Un visiteur pour la hierarchie de Field.
 */
public interface FieldVisitor {
    default void visit(ToOne field) {}
    default void visit(InversedOne field) {}
    default void visit(OneToMany field) {}
    default void visit(SimpleField field) {}
    default void visit(EnumField field) {}
    default void visit(OptionalToOne optionalToOne) {}
    default void visit(OptionalInversedOne optionalInversedOne) {}
}
