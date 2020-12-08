package cn.edu.tsinghua.dataset;

import cn.edu.tsinghua.conf.Conf;
import cn.edu.tsinghua.db.*;
import cn.edu.tsinghua.entity.Product;
import cn.edu.tsinghua.entity.ProductBrowse;
import cn.edu.tsinghua.entity.ProductOrder;
import cn.edu.tsinghua.entity.User;
import cn.edu.tsinghua.factory.ProductBrowseFactory;
import cn.edu.tsinghua.factory.ProductFactory;
import cn.edu.tsinghua.factory.ProductOrderFactory;
import cn.edu.tsinghua.factory.UserFactory;
import cn.edu.tsinghua.tool.ArrayGenerator;

import java.lang.reflect.Field;
import java.util.*;

public class DataSet {

    private Map<Class<?>, Index> indexMap;

    private Chunk<Product>[] productChunks;

    private HashBucket<Product>[] productBuckets;

    private Chunk<User>[] userChunks;

    private HashBucket<User>[] userBuckets;

    private Chunk<ProductBrowse>[] productBrowseChunks;

    private HashBucket<ProductBrowse>[] productBrowseBuckets;

    private Chunk<ProductOrder>[] productOrderChunks;

    private HashBucket<ProductOrder>[] productOrderBuckets;

    private int productOrderCount = 0;

    public DataSet(Map<Class<?>, Index> indexMap) throws Exception {
        this.indexMap = indexMap;
        // 加载 Product 数据
        Product[] products = new Product[Conf.ProductCount];
        for (int i = 0; i < Conf.ProductCount; i++) {
            products[i] = ProductFactory.product();
        }
        Index productIndex = indexMap.getOrDefault(Product.class, null);
        Pair<Chunk<Product>[], HashBucket<Product>[]> productPair = constructTable(products, productIndex, Product.class);
        productChunks = productPair.getK();
        productBuckets = productPair.getV();
        // 加载 User 数据
        User[] users = new User[Conf.UserCount];
        for (int i = 0; i < Conf.UserCount; i++) {
            users[i] = UserFactory.user();
        }
        Index userIndex = indexMap.getOrDefault(User.class, null);
        Pair<Chunk<User>[], HashBucket<User>[]> userPair = constructTable(users, userIndex, User.class);
        userChunks = userPair.getK();
        userBuckets = userPair.getV();
        // 加载访问和订单数据
        Random random = new Random();
        ProductBrowse[] productBrowses = new ProductBrowse[Conf.ProductBrowseCount];
        List<ProductOrder> productOrderList = new ArrayList<>();
        for (int i = 0; i < Conf.ProductBrowseCount; i++) {
            Product product = products[random.nextInt(products.length)];
            User user = users[random.nextInt(users.length)];
            ProductBrowse productBrowse = ProductBrowseFactory.productBrowse(user, product);
            productBrowses[i] = productBrowse;
            if (random.nextDouble() < 1.0 * Conf.ProductOrderCount / Conf.ProductBrowseCount) {
                productOrderList.add(ProductOrderFactory.productOrder(productBrowse));
            }
        }
        productOrderCount = productOrderList.size();
        ProductOrder[] productOrders = new ProductOrder[productOrderCount];
        for (int i = 0; i < productOrders.length; i++) {
            productOrders[i] = productOrderList.get(i);
        }
        Index productBrowseIndex = indexMap.getOrDefault(ProductBrowse.class, null);
        Pair<Chunk<ProductBrowse>[], HashBucket<ProductBrowse>[]> productBrowsePair = constructTable(productBrowses, productBrowseIndex, ProductBrowse.class);
        productBrowseChunks = productBrowsePair.getK();
        productBrowseBuckets = productBrowsePair.getV();
        Index productOrderIndex = indexMap.getOrDefault(ProductOrder.class, null);
        Pair<Chunk<ProductOrder>[], HashBucket<ProductOrder>[]> productOrderPair = constructTable(productOrders, productOrderIndex, ProductOrder.class);
        productOrderChunks = productOrderPair.getK();
        productOrderBuckets = productOrderPair.getV();
    }

    private <T> Pair<Chunk<T>[], HashBucket<T>[]> constructTable(T[] records, Index index, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        if (index != null && index.getType() == IndexType.HashCluster) {
            HashBucket<T>[] hashBuckets = constructHashBuckets(records, index.getFields(), clazz);
            List<Chunk<T>> chunkList = new ArrayList<>();
            for (HashBucket<T> hashBucket: hashBuckets) {
                chunkList.addAll(Arrays.asList(hashBucket.getChunks()));
            }
            Chunk<T>[] chunks = new Chunk[chunkList.size()];
            for (int i = 0; i < chunkList.size(); i++) {
                chunks[i] = chunkList.get(i);
            }
            return new Pair<>(chunks, hashBuckets);
        }
        if (index != null && index.getType() == IndexType.ZOrder) {
            ZOrderSorter<T> zOrderSorter = new ZOrderSorter<>();
            zOrderSorter.sort(records, index);
        }
        Chunk<T>[] chunks = constructChunks(records, clazz);
        return new Pair<>(chunks, null);
    }

    private <T> HashBucket<T>[] constructHashBuckets(T[] records, Field[] fields, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        List<T>[] bucketLists = new List[Conf.BucketCount];
        for (int i = 0; i < Conf.BucketCount; i++) {
            bucketLists[i] = new ArrayList<>();
        }
        for (T record: records) { // 数据分桶
            int hash = HashComputer.calculateHash(record, fields);
            bucketLists[hash >>> (32 - Conf.BucketPrefix)].add(record);
        }
        HashBucket<T>[] hashBuckets = new HashBucket[Conf.BucketCount];
        for (int i = 0; i < Conf.BucketCount; i++) {
            List<T> bucketList = bucketLists[i];
            T[] bucketRecords = ArrayGenerator.create(clazz, bucketList.size());
            for (int j = 0; j < bucketList.size(); j++) {
                bucketRecords[j] = bucketList.get(j);
            }
            Arrays.sort(bucketRecords, new FieldsComparator(fields));
            Chunk<T>[] chunks = constructChunks(bucketRecords, clazz);
            hashBuckets[i] = new HashBucket<>(chunks, clazz);
        }
        return hashBuckets;
    }


    private <T> Chunk<T>[] constructChunks(T[] records, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        int chunkSize = (int) Math.ceil(1.0 * records.length / Conf.ChunkSize);
        Chunk<T>[] chunks = new Chunk[chunkSize];
        for (int i = 0; i < chunkSize; i++) {
            int chunkDataSize = Math.min(Conf.ChunkSize, records.length - i * Conf.ChunkSize);
            T[] chunkData = ArrayGenerator.create(clazz, chunkDataSize);
            for (int j = 0; j < chunkData.length; j++) {
                chunkData[j] = records[j + i * Conf.ChunkSize];
            }
            chunks[i] = new Chunk<>(chunkData, clazz);
        }
        return chunks;
    }

    public long queryProductByStock() {
        long beginTime = System.nanoTime();
        List<Product> results = new ArrayList<>();
        int stockLowBound = 0, stockUpBound = 2000;
        for (Chunk<Product> productChunk : productChunks) {
            if (productChunk.getMin().getProductStock() > stockUpBound || productChunk.getMax().getProductStock() < stockLowBound) {
                continue;
            }
            if (productChunk.getMin().getProductStock() >= stockLowBound && productChunk.getMax().getProductStock() <= stockUpBound) {
                results.addAll(Arrays.asList(productChunk.getData()));
                continue;
            }
            Product[] chunkData = productChunk.getData();
            for (Product product : chunkData) {
                if (product.getProductStock() >= stockLowBound && product.getProductStock() <= stockUpBound) {
                    results.add(product);
                }
            }
        }
        long span = System.nanoTime() - beginTime;
        System.out.println(span + " " + results.size());
        return span;
    }

    public long queryProductByStockAndPrice() {
        long beginTime = System.nanoTime();
        List<Product> results = new ArrayList<>();
        int stockLowBound = 0, stockUpBound = 2000;
        double priceLowBound = 100.0, priceUpBound = 200.0;
        for (Chunk<Product> productChunk: productChunks) {
            if (productChunk.getMin().getProductStock() > stockUpBound || productChunk.getMax().getProductStock() < stockLowBound ||
                productChunk.getMin().getProductPrice() > priceUpBound || productChunk.getMax().getProductPrice() < priceLowBound) {
                continue;
            }
            if (productChunk.getMin().getProductStock() >= stockLowBound && productChunk.getMax().getProductStock() <= stockUpBound &&
                productChunk.getMin().getProductPrice() >= priceLowBound && productChunk.getMax().getProductPrice() <= priceUpBound) {
                results.addAll(Arrays.asList(productChunk.getData()));
            }
            Product[] chunkData = productChunk.getData();
            for (Product product : chunkData) {
                if (product.getProductStock() >= stockLowBound && product.getProductStock() <= stockUpBound &&
                    product.getProductPrice() >= priceLowBound && product.getProductPrice() <= priceUpBound) {
                    results.add(product);
                }
            }
        }
        long span = System.nanoTime() - beginTime;
        System.out.println(span + " " + results.size());
        return span;
    }

    public long queryUserById() {
        long beginTime = System.nanoTime();
        long userId = 214748367;
        for (Chunk<User> userChunk: userChunks) {
            if (userChunk.getMin().getUserId() > userId || userChunk.getMax().getUserId() < userId) {
                continue;
            }
            for (User user: userChunk.getData()) {
                if (user.getUserId() == userId) {
                    System.out.println(user.toString());
                }
            }
        }
        long span = System.nanoTime() - beginTime;
        System.out.println(span + " " + 0);
        return span;
    }

    public long queryProductOrder() {
        long beginTime = System.nanoTime();
        List<ProductOrder> results = new ArrayList<>();
        long targetId = productOrderChunks[22].getData()[0].getProductId();
        for (Chunk<ProductOrder> productOrderChunk: productOrderChunks) {
            if (productOrderChunk.getMin().getProductId() > targetId || productOrderChunk.getMax().getProductId() < targetId) {
                continue;
            }
            for (ProductOrder productOrder: productOrderChunk.getData()) {
                if (productOrder.getProductId() == targetId) {
                    results.add(productOrder);
                }
            }
        }
        long span = System.nanoTime() - beginTime;
        System.out.println(span + " " + results.size());
        return span;
    }

    public long queryProductOrderAndProductBrowse() throws Exception {
        long beginTime = System.nanoTime();
        HashBucket<ProductOrder>[] productOrderBuckets = this.productOrderBuckets;
        Index productOrderIndex = indexMap.getOrDefault(ProductOrder.class, null);
        Field[] productOrderJoinFields = new Field[]{
                ProductOrder.class.getDeclaredField("productId"),
                ProductOrder.class.getDeclaredField("userId"),
                ProductOrder.class.getDeclaredField("time")
        };
        if (productOrderIndex == null || cannotUseIndex(productOrderIndex, productOrderJoinFields)) {
            ProductOrder[] productOrders = new ProductOrder[productOrderCount];
            int index = 0;
            for (Chunk<ProductOrder> productOrderChunk : productOrderChunks) {
                ProductOrder[] productOrderChunkData = productOrderChunk.getData();
                System.arraycopy(productOrderChunkData, 0, productOrders, index, productOrderChunkData.length);
                index += productOrderChunkData.length;
            }
            productOrderBuckets = constructHashBuckets(productOrders, productOrderJoinFields, ProductOrder.class);
        }

        HashBucket<ProductBrowse>[] productBrowseBuckets = this.productBrowseBuckets;
        Index productBrowseIndex = indexMap.getOrDefault(ProductBrowse.class, null);
        Field[] productBrowseJoinFields = new Field[] {
                ProductBrowse.class.getDeclaredField("productId"),
                ProductBrowse.class.getDeclaredField("userId"),
                ProductBrowse.class.getDeclaredField("time")
        };
        if (productBrowseIndex == null || cannotUseIndex(productBrowseIndex, productBrowseJoinFields)) {
            ProductBrowse[] productBrowses = new ProductBrowse[Conf.ProductBrowseCount];
            int index = 0;
            for (Chunk<ProductBrowse> productBrowseChunk: productBrowseChunks) {
                ProductBrowse[] productBrowseChunkData = productBrowseChunk.getData();
                System.arraycopy(productBrowseChunkData, 0, productBrowses, index, productBrowseChunkData.length);
                index += productBrowseChunkData.length;
            }
            productBrowseBuckets = constructHashBuckets(productBrowses, productBrowseJoinFields, ProductBrowse.class);
        }
        int resultSize = 0;
        for (int i = 0; i < Conf.BucketCount; i++) {
            ProductOrder[] productOrders = productOrderBuckets[i].records();
            ProductBrowse[] productBrowses = productBrowseBuckets[i].records();
            int index1 = 0, index2 = 0;
            JoinComparator comparator = new JoinComparator(productOrderJoinFields, productBrowseJoinFields);
            List<Pair<ProductOrder, ProductBrowse>> resultList = new ArrayList<>();
            while (index1 < productOrders.length && index2 < productBrowses.length) {
                ProductOrder productOrder = productOrders[index1];
                ProductBrowse productBrowse = productBrowses[index2];
                int compareResult = comparator.compare(productOrder, productBrowse);
                if (compareResult == 0) {
                    resultList.add(new Pair<>(productOrder, productBrowse));
                    index1 += 1;
                    index2 += 1;
                } else if (compareResult < 0) {
                    index1 += 1;
                } else {
                    index2 += 1;
                }
            }
            resultSize += resultList.size();
        }
        long span = System.nanoTime() - beginTime;
        System.out.println(span + " " + resultSize);
        return resultSize;
    }

    private boolean cannotUseIndex(Index index, Field[] fields) {
        Set<String> indexFieldNameSet = new HashSet<>();
        for (Field field: index.getFields()) {
            indexFieldNameSet.add(field.getName());
        }
        for (Field field: fields) {
            if (indexFieldNameSet.contains(field.getName())) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Map<Class<?>, Index> indexMap = new HashMap<>();

        Index userIndex = new Index(new Field[]{
                User.class.getDeclaredField("userId")
        }, IndexType.ZOrder);
        indexMap.put(User.class, userIndex);

        Index productOrderIndex = new Index(new Field[]{
                ProductOrder.class.getDeclaredField("productId"),
                ProductOrder.class.getDeclaredField("userId"),
                ProductOrder.class.getDeclaredField("time")
        }, IndexType.HashCluster);
        indexMap.put(ProductOrder.class, productOrderIndex);

        Index productBrowseIndex = new Index(new Field[]{
                ProductBrowse.class.getDeclaredField("productId"),
                ProductBrowse.class.getDeclaredField("userId"),
                ProductBrowse.class.getDeclaredField("time"),
        }, IndexType.HashCluster);
        indexMap.put(ProductBrowse.class, productBrowseIndex);

        Index productIndex = new Index(new Field[]{
                Product.class.getDeclaredField("productStock"),
                Product.class.getDeclaredField("productPrice")
        }, IndexType.ZOrder);
        indexMap.put(Product.class, productIndex);

        DataSet dataSet = new DataSet(indexMap);
        dataSet.queryUserById();
        dataSet.queryProductByStockAndPrice();
        dataSet.queryProductByStock();
        dataSet.queryProductOrder();
        dataSet.queryProductOrderAndProductBrowse();
    }

}
