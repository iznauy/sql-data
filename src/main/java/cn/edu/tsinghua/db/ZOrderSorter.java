package cn.edu.tsinghua.db;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ZOrderSorter<T> {

    public void sort(T[] data, Index index) throws IllegalAccessException{
        Field[] fields = index.getFields();
        Map<T, Integer>[] fieldSeqList = new Map[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldSeqList[i] = sort(data, fields[i]);
        }
        Pair<T, Object>[] pairs = new Pair[data.length];
        for (int i = 0; i < data.length; i++) {
            List<Integer> orders = new ArrayList<>();
            for (int j = 0; j < fields.length; j++) {
                orders.add(fieldSeqList[j].get(data[i]));
            }
            String zOrder = getZOrder(orders);
            pairs[i] = new Pair<>(data[i], zOrder);
        }
        Arrays.sort(pairs, new PairComparator());
        for (int i = 0; i < data.length; i++) {
            data[i] = pairs[i].getK();
        }
    }

    private static String getZOrder(List<Integer> orders) {
        List<String> stringOrders = orders.stream().map(ZOrderSorter::toBinary).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            for (String stringOrder : stringOrders) {
                sb.append(stringOrder.charAt(i));
            }
        }
        return sb.toString();
    }

    private static String toBinary(int number) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < 32; i++){
            sBuilder.append(number & 1);
            number = number >>> 1;
        }
        return sBuilder.reverse().toString();
    }

    private Map<T, Integer> sort(T[] data, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Pair<Integer, Object>[] fieldValues = new Pair[data.length];
        for (int i = 0; i < data.length; i++) {
            fieldValues[i] = new Pair<>(i, field.get(data[i]));
        }
        Arrays.sort(fieldValues, new PairComparator());
        Map<T, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            resultMap.put(data[fieldValues[i].getK()], i);
        }
        return resultMap;
    }


}
