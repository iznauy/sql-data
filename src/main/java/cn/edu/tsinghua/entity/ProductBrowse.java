package cn.edu.tsinghua.entity;

import lombok.*;

import java.util.Date;
import java.util.Objects;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBrowse {

    private long userId;

    private long productId;

    private long time;

    private String source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductBrowse that = (ProductBrowse) o;
        return userId == that.userId &&
                productId == that.productId &&
                time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId, time);
    }

    @Override
    public String toString() {
        return String.format("%d,%d,%d,%s", userId, productId, time, source);
    }

    public static ProductBrowse fromString(String source) {
        String[] fields = source.split(",");
        return new ProductBrowse(Long.parseLong(fields[0]), Long.parseLong(fields[1]), Long.parseLong(fields[2]), fields[3]);
    }

}
