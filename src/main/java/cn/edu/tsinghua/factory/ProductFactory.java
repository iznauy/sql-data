package cn.edu.tsinghua.factory;

import cn.edu.tsinghua.entity.Product;
import cn.edu.tsinghua.tool.StringGenerator;

import java.util.Random;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class ProductFactory {

    private static Random random = new Random();

    public static Product product() {
        return new Product(productId(), productName(), productPrice(), productStock(), productDescription());
    }

    private static long productId() {
        return random.nextLong();
    }

    private static String productName() {
        return StringGenerator.generate(5 + random.nextInt(10));
    }

    private static double productPrice() {
        return random.nextDouble() * 500;
    }

    private static int productStock() {
        return random.nextInt() % 10000;
    }

    private static String productDescription() {
        return StringGenerator.generate(20 + random.nextInt(20));
    }

}
