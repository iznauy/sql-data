package cn.edu.tsinghua.db;

import java.util.Comparator;

public class ObjectComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof Long && o2 instanceof Long) {
            return ((Long) o1).compareTo((Long) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return ((Double) o1).compareTo((Double) o2);
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).compareTo((Integer) o2);
        }
        return 0;
    }
}
