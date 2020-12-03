package cn.edu.tsinghua.factory;

import cn.edu.tsinghua.entity.User;
import cn.edu.tsinghua.tool.StringGenerator;
import java.util.Random;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class UserFactory {

    private static Random random = new Random();

    public static User user() {
        return new User(userId(), username(), password(), address());
    }

    private static long userId() {
        return random.nextLong();
    }

    private static String username() {
        return StringGenerator.generate(5 + random.nextInt(10));
    }

    private static String password() {
        return StringGenerator.generate(16);
    }

    private static String address() {
        return StringGenerator.generate(20 + random.nextInt(20));
    }

}
