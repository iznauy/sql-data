package cn.edu.tsinghua.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
@Data
public class ProductOrder {

    private long userId;

    private long productId;

    private int count;

    private double amount;

    private Date time;

}
