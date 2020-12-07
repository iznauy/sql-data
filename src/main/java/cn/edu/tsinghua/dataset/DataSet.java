package cn.edu.tsinghua.dataset;

import cn.edu.tsinghua.conf.Conf;
import cn.edu.tsinghua.db.Chunk;
import cn.edu.tsinghua.db.Index;
import cn.edu.tsinghua.db.IndexType;
import cn.edu.tsinghua.db.ZOrderSorter;
import cn.edu.tsinghua.entity.Product;
import cn.edu.tsinghua.factory.ProductFactory;

import java.lang.reflect.Field;
import java.util.*;

public class DataSet {

    private Chunk<Product>[] productChunks;

    public DataSet(Map<Class<?>, Index> indexMap) throws Exception {
        Product[] products = new Product[Conf.ProductCount];
        for (int i = 0; i < Conf.ProductCount; i++) {
            products[i] = ProductFactory.product();
        }
        Index index = indexMap.getOrDefault(Product.class, null);
        if (index != null && index.getType() == IndexType.ZOrder) {
            ZOrderSorter<Product> zOrderSorter = new ZOrderSorter<>();
            zOrderSorter.sort(products, index);
        }
        productChunks = new Chunk[Conf.ProductCount / Conf.ChunkSize];
        for (int i = 0; i < productChunks.length; i++) {
            Product[] productOfChunk = new Product[Conf.ChunkSize];
            for (int j = i * Conf.ChunkSize; j < i * Conf.ChunkSize + Conf.ChunkSize; j++) {
                productOfChunk[j - i * Conf.ChunkSize] = products[j];
            }
            productChunks[i] = new Chunk<>(productOfChunk, Product.class);
        }
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

    public static void main(String[] args) throws Exception {
//        Index index = new Index(new Field[]{Product.class.getDeclaredField("productStock"), Product.class.getDeclaredField("productPrice")}, IndexType.ZOrder);
//        Map<Class<?>, Index> indexMap = new HashMap<>();
//        indexMap.put(Product.class, index);
//        DataSet dataSet = new DataSet(indexMap);
        DataSet dataSet = new DataSet(new HashMap<>());
//        dataSet.queryProductByStockAndPrice();
        dataSet.queryProductByStock();
    }

}
