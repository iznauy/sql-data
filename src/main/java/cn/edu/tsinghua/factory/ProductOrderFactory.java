package cn.edu.tsinghua.factory;

import cn.edu.tsinghua.entity.ProductBrowse;
import cn.edu.tsinghua.entity.ProductOrder;

import java.util.Random;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class ProductOrderFactory {

    private static final Random random = new Random();

    public static ProductOrder productOrder(ProductBrowse browse) {
        return new ProductOrder(browse.getUserId(), browse.getProductId(), count(), amount(), browse.getTime());
    }

    private static int count() {
        return 1 + random.nextInt(50);
    }


    private static double amount() {
        return 20 + random.nextDouble() * 1000;
    }

}
