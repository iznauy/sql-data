package cn.edu.tsinghua;

import cn.edu.tsinghua.conf.Conf;
import cn.edu.tsinghua.entity.Product;
import cn.edu.tsinghua.entity.ProductBrowse;
import cn.edu.tsinghua.entity.ProductOrder;
import cn.edu.tsinghua.entity.User;
import cn.edu.tsinghua.factory.ProductBrowseFactory;
import cn.edu.tsinghua.factory.ProductFactory;
import cn.edu.tsinghua.factory.ProductOrderFactory;
import cn.edu.tsinghua.factory.UserFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
public class Main {

    public static void main(String[] args) throws IOException {
        List<Product> products = new ArrayList<>(Conf.ProductCount);
        List<User> users = new ArrayList<>(Conf.UserCount);
        List<ProductBrowse> productBrowses = new ArrayList<>(Conf.ProductBrowseCount);
        List<ProductOrder> productOrders = new ArrayList<>(Conf.ProductOrderCount);
        for (int i = 0; i < Conf.ProductCount; i++) {
            products.add(ProductFactory.product());
        }
        for (int i = 0; i < Conf.UserCount; i++) {
            users.add(UserFactory.user());
        }
        Random random = new Random();
        for (int i = 0; i < Conf.ProductBrowseCount; i++) {
            Product product = products.get(random.nextInt(products.size()));
            User user = users.get(random.nextInt(users.size()));
            ProductBrowse productBrowse = ProductBrowseFactory.productBrowse(user, product);
            if (random.nextDouble() < 1.0 * Conf.ProductOrderCount / Conf.ProductBrowseCount) { // 浏览有概率转化为订单
                ProductOrder productOrder = ProductOrderFactory.productOrder(productBrowse);
                productOrders.add(productOrder);
            }
            productBrowses.add(productBrowse);
        }

        writeToFile(products, Conf.ProductTableName);
        writeToFile(users, Conf.UserTableName);
        writeToFile(productBrowses, Conf.ProductBrowseTableName);
        writeToFile(productOrders, Conf.ProductOrderTableName);
    }

    private static void writeToFile(List<?> objects, String TableName) throws IOException {
        File file = new File(String.format(Conf.FileTemplate, Conf.TablePath, TableName));
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        for (Object o: objects) {
            writer.write(o.toString() + "\n");
        }
        writer.close();
    }

}


