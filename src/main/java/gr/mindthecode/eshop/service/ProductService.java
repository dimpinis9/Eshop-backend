package gr.mindthecode.eshop.service;

import gr.mindthecode.eshop.model.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    Product createOrUpdateProduct(Integer id,Product product) throws Exception;
    Product deleteProduct(Integer id);
    public abstract Product getProductById(Integer id);
    Page<Product> getProducts(String description,
                              String category,
                              int page,
                              int size,
                              String sort);
}
