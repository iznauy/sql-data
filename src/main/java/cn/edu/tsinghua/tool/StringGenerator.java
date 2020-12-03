package cn.edu.tsinghua.tool;

import java.util.Random;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class StringGenerator {

    private static final Random random = new Random();

    public static String generate(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char)('a' + random.nextInt(26)));
        }
        return builder.toString();
    }

}
