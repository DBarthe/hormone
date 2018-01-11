package hormone;

import hormone.exception.MappingException;
import hormone.field.Field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Un mapping permet de configurer un mapper.
 * Il contient le nom de table et une HashMap de Fields
 */
public class Mapping {

    /** order or keys must be preserved (so we use LinkedHashMap) */
    private Map<String, Field> fieldsMap;

    private String tableName;

    public Mapping(String tableName) {
        this.tableName = tableName;
        fieldsMap = new LinkedHashMap<>();
    }

    public Object getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnNames(String fieldName) {
        if (fieldsMap.containsKey(fieldName)) {
            return fieldsMap.get(fieldName).getColumnNames();
        }
        else {
            throw new MappingException("field name '" + fieldName + "' does not exist");
        }
    }

    public List<String> getColumnNames() {
        return fieldsMap.values().stream().map(Field::getColumnNames).flatMap(List::stream).collect(Collectors.toList());
    }

    /** must always return fields in the same order */
    public List<Field> getFields() {
        return new ArrayList<>(fieldsMap.values());
    }

    public Field getField(String name) {
        return fieldsMap.getOrDefault(name, null);
    }

    public void addField(Field field) {
        fieldsMap.put(field.getName(), field);
    }
}
