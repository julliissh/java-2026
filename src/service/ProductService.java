package service;

import product.Product;
import java.util.List;

public interface ProductService extends CRUDService<Product> {

    List<Product> findAllSortedByNameAndByPrice();
}
