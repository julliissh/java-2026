package service;

import product.Product;

import java.util.Comparator;
import java.util.List;

public class InMemoryProductService extends InMemoryCrudService<Product> implements ProductService {

    @Override
    public List<Product> findAllSortedByNameAndByPrice() {
        return findAllSorted(Comparator.comparing(Product::getName)
                .thenComparingDouble(Product::getPrice));
    }
}
