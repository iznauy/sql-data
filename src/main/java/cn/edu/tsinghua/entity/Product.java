package cn.edu.tsinghua.entity;

import lombok.Data;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
@Data
public class Product {

    private long productId;

    private String productName;

    private double productPrice;

    private int productStock;

    private String productDescription;

}
