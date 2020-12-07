package cn.edu.tsinghua.db;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * Created on 2020-12-07.
 * Description:
 *
 * @author iznauy
 */
@Data
public class Index {

    private Field[] fields;

    private IndexType type;

}
