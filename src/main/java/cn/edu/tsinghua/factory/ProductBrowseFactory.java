package cn.edu.tsinghua.factory;

import cn.edu.tsinghua.entity.Product;
import cn.edu.tsinghua.entity.ProductBrowse;
import cn.edu.tsinghua.entity.User;

import java.util.Random;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class ProductBrowseFactory {

    private static final Random random = new Random();

    private static final String[] sources;

    static {
        sources = new String[] {
                "bilibili",
                "taobao",
                "jd",
                "tianmao",
                "pdd",
                "suning"
        };
    }

    public static ProductBrowse productBrowse(User user, Product product) {
        return new ProductBrowse(user.getUserId(), product.getProductId(), time(), source());
    }

    private static long time() {
        return random.nextLong();
    }

    private static String source() {
        return sources[random.nextInt(sources.length)];
    }


}
