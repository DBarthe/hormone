package hormone;

import hormone.annotation.Column;

import java.util.HashMap;
import java.util.UUID;

/**
 * Classe de base pour tous les objets à mapper.
 *
 * Contient : l'id, le boolean dirty, et une hashmap d'objets internes pour les besoins de l'hormone
 */
public abstract class Model {

    /**
     * @var Les id sont des UUID stockés dans des strings
     */
    @Column
    private String id;

    /**
     *  L'objet est considéré comme sale si l'on sait qu'il diverge avec la ligne correspondante dans la base de données
     *  C'est une fonctionnalité totalement optionelle.
     *  Elle permet de tracker activement les modifications et d'effectuer des updates de façon transparente.
     */
    private boolean dirty = false;

    /**
     * Internal map to store internal data.
     * The key is the field name.
     * This map is allocated only on demand.
     * We store things such as raw foreign keys.
     */
    private HashMap<String, Object> internalFields = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGeneratedId() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Get an internal field
     */
    public Object getInternalField(String fieldName) {
        if (internalFields == null) {
            return null;
        }
        return internalFields.getOrDefault(fieldName, null);
    }

    public boolean isDirty() {
        return dirty;
    }

    /**
     * L'utilisateur peut indiquer volontairement que l'objet est dirty.
     * Il peut être appelé depuis la classe Model metier par exemple.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Check if internal field exists
     */
    public boolean hasInternalField(String fieldName) {
        return internalFields != null && internalFields.containsKey(fieldName);
    }

    /**
     * Set an internal field, allocating the internalFields map if not allocated yet.
     */
    public void setInternalField(String fieldName, Object fieldValue) {
        if (internalFields == null) {
            internalFields = new HashMap<>();
        }
        internalFields.put(fieldName, fieldValue);
    }
}
