package cn.edu.tsinghua.db;

import java.lang.reflect.Field;
import java.util.Comparator;

public class FieldsComparator implements Comparator<Object> {

    private Field[] fields;

    public FieldsComparator(Field[] fields) {
        this.fields = fields;
        for (Field field: fields) {
            field.setAccessible(true);
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        try {
            return compare(o1, o2, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int compare(Object o1, Object o2, int index) throws IllegalAccessException {
        if (index >= fields.length) {
            return 0;
        }
        int result = fieldCompare(o1, o2, fields[index]);
        if (result == 0) {
            result = compare(o1, o2, index + 1);
        }
        return result;
    }

    private int fieldCompare(Object o1, Object o2, Field field) throws IllegalAccessException {
        o1 = field.get(o1);
        o2 = field.get(o2);
        if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof Long && o2 instanceof Long) {
            return ((Long) o1).compareTo((Long) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return ((Double) o1).compareTo((Double) o2);
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).compareTo((Integer) o2);
        }
        throw new IllegalArgumentException();
    }

}
