package cn.edu.tsinghua.db;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created on 2020-12-08.
 * Description:
 *
 * @author iznauy
 */
public class HashComputer {

    public static <T> int calculateHash(T o, Field[] fields) throws IllegalAccessException {
        Object[] fieldValues = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            fieldValues[i] = field.get(o);
        }
        return Objects.hash(fieldValues);
    }

}
