package cn.edu.tsinghua.entity;

import lombok.*;

import java.lang.reflect.Field;
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
public class Product {

    private long productId;

    private String productName;

    private double productPrice;

    private int productStock;

    private String productDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%f,%d,%s", productId, productName, productPrice, productStock, productDescription);
    }

    public static Product fromString(String source) {
        String[] fields = source.split(",");
        return new Product(Long.parseLong(fields[0]), fields[1], Double.parseDouble(fields[2]), Integer.parseInt(fields[3]), fields[4]);
    }
}
