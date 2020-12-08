package cn.edu.tsinghua.db;

import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

/**
 * Created on 2020-12-08.
 * Description:
 *
 * @author iznauy
 */
public class JoinComparator {

    private final Field[] fields1;

    private final Field[] fields2;

    public JoinComparator(Field[] fields1, Field[] fields2) {
        this.fields1 = fields1;
        this.fields2 = fields2;
        for (Field field: fields1) {
            field.setAccessible(true);
        }
        for (Field field: fields2) {
            field.setAccessible(true);
        }
    }

    public int compare(Object o1, Object o2) {
        try {
            return compare(o1, o2, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int compare(Object o1, Object o2, int index) throws IllegalAccessException {
        if (index >= fields1.length) {
            return 0;
        }
        int result = fieldCompare(o1, o2, fields1[index], fields2[index]);
        if (result == 0) {
            result = compare(o1, o2, index + 1);
        }
        return result;
    }

    private int fieldCompare(Object o1, Object o2, Field field1, Field field2) throws IllegalAccessException {
        o1 = field1.get(o1);
        o2 = field2.get(o2);
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
