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




    /**
     * 下面是和数据集有关的配置
     */
    public static final String TablePath = "D:\\Projects\\Java\\sql-data";

    public static final String ProductTableName = "product";

    public static final String UserTableName = "user";

    public static final String ProductOrderTableName = "product_order";

    public static final String ProductBrowseTableName = "product_browse";

    public static final String FileTemplate = "%s" + File.separator + "%s.txt";

    public static final int UserCount = 100000;

    public static final int ProductCount = 50000;

    public static final int ProductOrderCount = 500000;

    public static final int ProductBrowseCount = 2000000;

}
