package cn.edu.tsinghua.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Pair<K, V> {

    private K k;

    private V v;

}
