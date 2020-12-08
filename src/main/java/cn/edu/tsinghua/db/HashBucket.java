package cn.edu.tsinghua.db;

import cn.edu.tsinghua.tool.ArrayGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * Created on 2020-12-08.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashBucket<T> {

    private Chunk<T>[] chunks;

    private Class<T> clazz;

    public T[] records() {
        int dataSize = 0;
        for (Chunk<T> chunk: chunks) {
            dataSize += chunk.getData().length;
        }
        T[] records = ArrayGenerator.create(clazz, dataSize);
        int index = 0;
        for (Chunk<T> chunk: chunks) {
            T[] chunkRecords = chunk.getData();
            System.arraycopy(chunkRecords, 0, records, index, chunkRecords.length);
            index += chunkRecords.length;
        }
        return records;
    }

}
