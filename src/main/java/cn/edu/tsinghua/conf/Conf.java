package cn.edu.tsinghua.conf;

import java.io.File;

/**
 * Created on 2020-12-03.
 * Description:
 *
 * @author iznauy
 */
public class Conf {

    /**
     * 和数据库有关的配置
     */
    public static final int ChunkSize = 100;

    public static final int BucketCount = 64;

    public static final int BucketPrefix = 6;

    /**
     * 下面是和数据集有关的配置
     */
    public static final String TablePath = "D:\\Projects\\Java\\sql-data";

    public static final String ProductTableName = "product";

    public static final String UserTableName = "user";

    public static final String ProductOrderTableName = "product_order";

    public static final String ProductBrowseTableName = "product_browse";

    public static final String FileTemplate = "%s" + File.separator + "%s.txt";

    public static final int UserCount = 128000;

    public static final int ProductCount = 64000;

    public static final int ProductOrderCount = 640000;

    public static final int ProductBrowseCount = 2560000;

}
