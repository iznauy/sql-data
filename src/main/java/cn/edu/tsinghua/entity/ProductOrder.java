package cn.edu.tsinghua.entity;

import lombok.*;

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
public class ProductOrder {

    private long userId;

    private long productId;

    private int count;

    private double amount;

    private long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductOrder that = (ProductOrder) o;
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
        return String.format("%d,%d,%d,%f,%d", userId, productId, count, amount, time);
    }

    public static ProductOrder fromString(String source) {
        String[] fields = source.split(",");
        return new ProductOrder(Long.parseLong(fields[0]), Long.parseLong(fields[1]), Integer.parseInt(fields[2]), Double.parseDouble(fields[3]), Long.parseLong(fields[4]));
    }

}
