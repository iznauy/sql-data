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
        assert fields.length == 5;
        return new Product(Long.valueOf(fields[0]), fields[1], Double.valueOf(fields[2]), Integer.valueOf(fields[3]), fields[4]);
    }

}
