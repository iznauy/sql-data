package cn.edu.tsinghua.tool;

import java.lang.reflect.Array;

/**
 * Created on 2020-12-08.
 * Description:
 *
 * @author iznauy
 */
public class ArrayGenerator {

    public static <T> T[] create(Class<T> type, int size) {
        return (T[]) Array.newInstance(type, size);
    }

}
