package cn.edu.tsinghua.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;

@NoArgsConstructor
@Getter
@Setter
public class Chunk<T> {

    private T[] data;

    private T min;

    private T max;

    public Chunk(T[] data, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        this.data = data;
        this.min = clazz.newInstance();
        this.max = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            Pair<Void, Object>[] pairs = new Pair[data.length];
            for (int i = 0; i < data.length; i++) {
                pairs[i] = new Pair<>(null, field.get(data[i]));
            }
            Arrays.sort(pairs, new PairComparator());
            field.set(this.min, pairs[0].getV());
            field.set(this.max, pairs[pairs.length - 1].getV());
        }
    }



}
