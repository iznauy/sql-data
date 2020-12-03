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
public class ProductBrowse {

    private long userId;

    private long productId;

    private Date time;

    private String source;

}
