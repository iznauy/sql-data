package cn.edu.tsinghua.db;

import java.util.Comparator;

public class PairComparator implements Comparator<Pair<?, Object>> {

    @Override
    public int compare(Pair<?, Object> o1, Pair<?, Object> o2) {
        if (o1.getV() instanceof String && o2.getV() instanceof String) {
            return ((String) o1.getV()).compareTo((String) o2.getV());
        } else if (o1.getV() instanceof Long && o2.getV() instanceof Long) {
            return ((Long) o1.getV()).compareTo((Long) o2.getV());
        } else if (o1.getV() instanceof Double && o2.getV() instanceof Double) {
            return ((Double) o1.getV()).compareTo((Double) o2.getV());
        } else if (o1.getV() instanceof Integer && o2.getV() instanceof Integer) {
            return ((Integer) o1.getV()).compareTo((Integer) o2.getV());
        }
        System.out.println("This should not happened!");
        System.out.println(o1.getV());
        return 0;
    }
}
